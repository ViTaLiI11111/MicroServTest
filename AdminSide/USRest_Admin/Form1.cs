using System;
using System.Data;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Collections.Generic;
using System.Linq;

namespace USRest_Admin
{
    public partial class Form1 : Form
    {
        private TimerVelocity timerVelocity;
        // ====== поля ======
        private readonly DataTable _dtDishes = new DataTable();
        private readonly DataTable _dtCategories = new DataTable();

        // змінюй на свій базовий URL або заведи TextBox для цього
        private const string DefaultBaseUrl = "http://localhost:5045";

        private MenuApiClient _api;

        public Form1()
        {
            InitializeComponent();
            timerVelocity = new TimerVelocity(labelMessage);

            // готуємо гріди
            _dtDishes.Columns.Add("Id");
            _dtDishes.Columns.Add("Назва");
            _dtDishes.Columns.Add("Ціна");
            _dtDishes.Columns.Add("Гострота");
            _dtDishes.Columns.Add("Категорія");
            _dtDishes.Columns.Add("Задній фон");
            _dtDishes.Columns.Add("Зображення", typeof(Image));

            _dtCategories.Columns.Add("Id категорії");
            _dtCategories.Columns.Add("Назва категорії");

            // якщо в тебе гріди мають інші імена — заміни
            dataGridView1.DataSource = _dtDishes;
            dataGridView2.DataSource = _dtCategories;
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            // Якщо маєш TextBox для базового URL – зчитай із нього
            // var baseUrl = txtBaseUrl.Text.Trim();
            var baseUrl = DefaultBaseUrl;

            _api = new MenuApiClient(baseUrl);
        }

        // =====================================================================
        //                              DISHES
        // =====================================================================

        private async void Insert_Click(object sender, EventArgs e)
        {
            try
            {
                if (!ValidateDishInputs())
                {
                    return;
                }

                int id = int.Parse(textBox_id.Text);

                // сформуємо запит
                CreateDishRequest req = new CreateDishRequest();
                req.Id = int.Parse(textBox_id.Text);
                req.Title = textBox_title.Text.Trim();
                req.Price = decimal.Parse(textBox_price.Text.Trim());
                req.Pepper = textBox_spicy.Text.Trim();
                req.Color = textBox_color.Text.Trim();
                req.CategoryId = int.Parse(textBox_category.Text.Trim());
                req.ImageBase64 = EncodeImageToBase64(imageBox.Image); // може бути null => ок

                // визначимо, створюємо чи оновлюємо
                DishDto existing = null;
                try
                {
                    existing = await _api.GetDishAsync(id);
                }
                catch
                {
                    existing = null;
                }

                DishDto resp;

                if (existing == null)
                {
                    // створення (Id дає БД)
                    resp = await _api.CreateDishAsync(req);
                    if (resp == null)
                    {
                        // деякі бек-конфи віддають 201 без тіла — перечитаємо по назві+категорії або просто оновимо грід
                        MessageBox.Show("Створено страву (можливо без тіла відповіді). Оновлю грід.", "OK",
                            MessageBoxButtons.OK, MessageBoxIcon.Information);
                    }
                    else
                    {
                        MessageBox.Show("Створено страву Id = " + resp.Id, "OK",
                            MessageBoxButtons.OK, MessageBoxIcon.Information);
                        LoadDishToForm(resp);
                    }
                }
                else
                {
                    // оновлення за Id
                    resp = await _api.UpdateDishAsync(id, req);
                    if (resp == null)
                    {
                        MessageBox.Show("Оновлено страву (можливо без тіла відповіді).", "OK",
                            MessageBoxButtons.OK, MessageBoxIcon.Information);
                    }
                    else
                    {
                        MessageBox.Show("Оновлено страву Id = " + resp.Id, "OK",
                            MessageBoxButtons.OK, MessageBoxIcon.Information);
                        LoadDishToForm(resp);
                    }
                }

                await ReloadDishesGridAsync();
            }
            catch (Exception ex)
            {
                ShowTopMessage("Помилка при збереженні: " + ex.Message);
            }
        }

        private async void Retrieve_Click(object sender, EventArgs e)
        {
            try
            {
                int id;
                if (!int.TryParse(textBox_id.Text, out id))
                {
                    MessageBox.Show("Вкажи коректний Id страви.");
                    return;
                }

                var dish = await _api.GetDishAsync(id);
                if (dish == null)
                {
                    MessageBox.Show("Страву не знайдено.");
                    return;
                }

                LoadDishToForm(dish);
            }
            catch (Exception ex)
            {
                ShowTopMessage("Помилка при отриманні: " + ex.Message);
            }
        }

        private async void Delete_Click(object sender, EventArgs e)
        {
            try
            {
                int id;
                if (!int.TryParse(textBox_id.Text, out id))
                {
                    MessageBox.Show("Вкажи коректний Id страви.");
                    return;
                }

                if (MessageBox.Show("Видалити страву?", "Підтвердження",
                        MessageBoxButtons.YesNo, MessageBoxIcon.Question) != DialogResult.Yes)
                {
                    return;
                }

                await _api.DeleteDishAsync(id);
                ClearDishFields();
                await ReloadDishesGridAsync();
                MessageBox.Show("Страву видалено.");
            }
            catch (Exception ex)
            {
                ShowTopMessage("Помилка при видаленні: " + ex.Message);
            }
        }

        private async void ExportToGridView_Click(object sender, EventArgs e)
        {
            try
            {
                await ReloadDishesGridAsync();
            }
            catch (Exception ex)
            {
                ShowTopMessage("Помилка завантаження страв: " + ex.Message);
            }
        }

        private async Task ReloadDishesGridAsync()
        {
            _dtDishes.Rows.Clear();

            var list = await _api.GetDishesAsync();   // List<DishDto>
            if (list == null || list.Count == 0) return;

            foreach (var d in list)
            {
                var row = _dtDishes.NewRow();
                row["Id"] = d.Id;
                row["Назва"] = d.Title;
                row["Ціна"] = d.Price;
                row["Гострота"] = d.Pepper;
                row["Категорія"] = d.CategoryId;
                row["Задній фон"] = d.Color;

                if (!string.IsNullOrEmpty(d.ImageBase64))
                {
                    try { row["Зображення"] = DecodeImage(d.ImageBase64); }
                    catch { row["Зображення"] = CreatePlaceholderImage("Помилка\nзображення"); }
                }
                else
                {
                    row["Зображення"] = CreatePlaceholderImage("Немає\nзображення");
                }

                _dtDishes.Rows.Add(row);
            }
        }


        private void Clear_Click(object sender, EventArgs e)
        {
            ClearDishFields();
        }

        private void Browse_Click(object sender, EventArgs e)
        {
            using (var ofd = new OpenFileDialog())
            {
                ofd.Title = "Виберіть зображення";
                ofd.Filter = "Файли зображень (*.png;*.jpg;*.jpeg)|*.png;*.jpg;*.jpeg|Всі файли (*.*)|*.*";
                if (ofd.ShowDialog() == DialogResult.OK)
                {
                    var img = new Bitmap(ofd.FileName);
                    imageBox.Image = img.GetThumbnailImage(330, 220, null, IntPtr.Zero);
                }
            }
        }

        private void ClearDishFields()
        {
            imageBox.Image = null;
            textBox_id.Text = "";
            textBox_title.Text = "";
            textBox_price.Text = "";
            textBox_spicy.Text = "";
            textBox_color.Text = "";
            textBox_category.Text = "";
        }

        private bool ValidateDishInputs()
        {
            // прості перевірки — підкоригуй під свої вимоги
            int tmpInt;
            decimal tmpDec;

            if (!int.TryParse(textBox_id.Text.Trim(), out tmpInt))
            {
                MessageBox.Show("Некоректний Id.");
                return false;
            }
            if (textBox_title.Text.Trim().Length == 0)
            {
                MessageBox.Show("Назва обов'язкова.");
                return false;
            }
            if (!decimal.TryParse(textBox_price.Text.Trim(), out tmpDec))
            {
                MessageBox.Show("Ціна некоректна.");
                return false;
            }
            if (!Regex.IsMatch(textBox_color.Text.Trim(), "^#[0-9a-fA-F]{3,8}$"))
            {
                MessageBox.Show("Колір має бути у форматі #RRGGBB.");
                return false;
            }
            if (!int.TryParse(textBox_category.Text.Trim(), out tmpInt))
            {
                MessageBox.Show("Id категорії некоректний.");
                return false;
            }
            return true;
        }

        private void LoadDishToForm(DishDto d)
        {
            if (d == null) return;

            textBox_id.Text = d.Id.ToString();
            textBox_title.Text = d.Title;
            textBox_price.Text = d.Price.ToString();
            textBox_spicy.Text = d.Pepper;
            textBox_color.Text = d.Color;
            textBox_category.Text = d.CategoryId.ToString();

            imageBox.Image = string.IsNullOrEmpty(d.ImageBase64)
                ? null
                : SafeDecodeImage(d.ImageBase64);
        }

        // =====================================================================
        //                             CATEGORIES
        // =====================================================================

        private async Task LoadCategoriesToGridAsync()
        {
            try
            {
                var list = await _api.GetCategoriesAsync();
                if (list == null) list = new List<CategoryDto>();
                dataGridView2.AutoGenerateColumns = true;
                dataGridView2.DataSource = list;
            }
            catch (Exception ex)
            {
                MessageBox.Show("Помилка завантаження категорій: " + ex.Message);
            }
        }

        private bool ValidateInputsCategory(out int id, out string title)
        {
            id = 0;
            title = string.Empty;

            if (!int.TryParse(textBox_id_category.Text, out id) || id <= 0)
            {
                MessageBox.Show("Id категорії має бути цілим числом ≥ 1.");
                return false;
            }

            title = (textBox_title_category.Text ?? string.Empty).Trim();
            if (title.Length == 0)
            {
                MessageBox.Show("Назва категорії обов’язкова.");
                return false;
            }

            // Додаткова перевірка формату (можеш прибрати, якщо зайве)
            if (!Regex.IsMatch(title, @"^[\p{L}\p{M}\s'\-]+$"))
            {
                MessageBox.Show("Назва містить недопустимі символи.");
                return false;
            }

            return true;
        }

        // Кнопка: Ввести/Оновити
        private async void InsertCategory_Click(object sender, EventArgs e)
        {
            int id;
            string title;
            if (!ValidateInputsCategory(out id, out title)) return;

            try
            {
                var req = new CreateCategoryRequest { Id = id, Title = title };

                // Перевіримо, чи є вже така категорія
                var existing = await _api.GetCategoryAsync(id);
                if (existing == null)
                {
                    var created = await _api.CreateCategoryAsync(req);
                    MessageBox.Show("Створено категорію Id=" + created.Id);
                }
                else
                {
                    var updated = await _api.UpdateCategoryAsync(id, req);
                    MessageBox.Show("Оновлено категорію Id=" + updated.Id);
                }

                await LoadCategoriesToGridAsync();
                ClearCategoryFields_Click(sender, e);
            }
            catch (Exception ex)
            {
                MessageBox.Show("Помилка з категорією: " + ex.Message);
            }
        }

        // Кнопка: Повернути (отримати по Id в текстбоксі)
        private async void RetrieveCategory_Click(object sender, EventArgs e)
        {
            int id;
            if (!int.TryParse(textBox_id_category.Text, out id) || id <= 0)
            {
                MessageBox.Show("Вкажіть коректний Id.");
                return;
            }

            try
            {
                var cat = await _api.GetCategoryAsync(id);
                if (cat == null)
                {
                    MessageBox.Show("Категорію не знайдено.");
                    return;
                }

                textBox_id_category.Text = cat.Id.ToString();
                textBox_title_category.Text = cat.Title;
            }
            catch (Exception ex)
            {
                MessageBox.Show("Помилка: " + ex.Message);
            }
        }

        // Кнопка: Видалити
        private async void DeleteCategory_Click(object sender, EventArgs e)
        {
            int id;
            if (!int.TryParse(textBox_id_category.Text, out id) || id <= 0)
            {
                MessageBox.Show("Вкажіть коректний Id.");
                return;
            }

            var confirm = MessageBox.Show(
                "Видалити категорію " + id + "?",
                "Підтвердження",
                MessageBoxButtons.YesNo,
                MessageBoxIcon.Question);

            if (confirm != DialogResult.Yes) return;

            try
            {
                await _api.DeleteCategoryAsync(id);
                await LoadCategoriesToGridAsync();
                ClearCategoryFields_Click(sender, e);
            }
            catch (Exception ex)
            {
                MessageBox.Show("Помилка видалення: " + ex.Message);
            }
        }

        // Кнопка: Показати у DataGridView
        private async void ExportToDataGridViewCategory_Click(object sender, EventArgs e)
        {
            await LoadCategoriesToGridAsync();
        }

        // Кнопка: Очистити поля
        private void ClearCategoryFields_Click(object sender, EventArgs e)
        {
            textBox_id_category.Text = string.Empty;
            textBox_title_category.Text = string.Empty;
        }

        // =====================================================================
        //                         image / helpers
        // =====================================================================

        private static string EncodeImageToBase64(Image img)
        {
            if (img == null) return null;

            using (var ms = new MemoryStream())
            {
                // PNG без втрат; якщо треба JPEG – заміни та додай якість
                img.Save(ms, ImageFormat.Png);
                var bytes = ms.ToArray();
                return Convert.ToBase64String(bytes);
            }
        }

        private static Image SafeDecodeImage(string base64)
        {
            try
            {
                return DecodeImage(base64);
            }
            catch
            {
                return null;
            }
        }

        private static Image DecodeImage(string base64)
        {
            var bytes = Convert.FromBase64String(base64);
            using (var ms = new MemoryStream(bytes))
            {
                // створюємо копію, щоб не тримати потік відкритим
                using (var bmp = new Bitmap(ms))
                {
                    return new Bitmap(bmp);
                }
            }
        }

        private static Bitmap CreatePlaceholderImage(string text)
        {
            Bitmap bmp = new Bitmap(140, 90);
            using (Graphics g = Graphics.FromImage(bmp))
            {
                g.Clear(Color.White);
                using (Font font = new Font("Arial", 9))
                using (StringFormat sf = new StringFormat())
                {
                    sf.Alignment = StringAlignment.Center;
                    sf.LineAlignment = StringAlignment.Center;
                    g.DrawString(text, font, Brushes.Gray, new RectangleF(0, 0, 140, 90), sf);
                }
            }
            return bmp;
        }

        private void ShowTopMessage(string msg)
        {
            // якщо є спеціальний labelMessage – можна писати туди
            // інакше просто MessageBox
            MessageBox.Show(msg, "Помилка", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }

        // ================== ПОДІЇ, яких бракувало ==================

        private async void DeleteAll_Click(object sender, EventArgs e)
        {
            if (MessageBox.Show("Видалити всі страви?", "Підтвердження", MessageBoxButtons.YesNo) != DialogResult.Yes)
                return;

            try
            {
                var dishes = await _api.GetDishesAsync(); // List<DishDto>
                if (dishes != null)
                {
                    foreach (var d in dishes)
                        await _api.DeleteDishAsync(d.Id);
                }

                await ReloadDishesGridAsync();
                ResetControls(isCategory: false);
                timerVelocity.ShowTemporaryMessage("Всі страви видалені.");
            }
            catch (Exception ex)
            {
                timerVelocity.ShowTemporaryMessage("Помилка при видаленні страв: " + ex.Message);
            }
        }

        private async void DeleteAllCategory_Click(object sender, EventArgs e)
        {
            if (MessageBox.Show("Видалити всі категорії (які не використовуються)?",
                                "Підтвердження", MessageBoxButtons.YesNo) != DialogResult.Yes)
                return;

            try
            {
                var cats = await _api.GetCategoriesAsync(); // List<CategoryDto>
                var dishes = await _api.GetDishesAsync();     // щоб не ламати FK

                var busyCategoryIds = new HashSet<int>(dishes?.Select(x => x.CategoryId) ?? Enumerable.Empty<int>());

                if (cats != null)
                {
                    foreach (var c in cats)
                    {
                        // категорії, що використовуються стравами, пропускаємо
                        if (!busyCategoryIds.Contains(c.Id))
                            await _api.DeleteCategoryAsync(c.Id);
                    }
                }

                await LoadCategoriesToGridAsync();
                timerVelocity.ShowTemporaryMessage("Категорії, що не використовуються, видалені.");
            }
            catch (Exception ex)
            {
                timerVelocity.ShowTemporaryMessage("Помилка при видаленні категорій: " + ex.Message);
            }
        }

        private void UserDelete_Click(object sender, EventArgs e)
        {
            // Раніше тут було видалення гілки Admin у Firebase.
            // У нашій версії зробимо простий «вихід/лог-аут».
            if (MessageBox.Show("Вийти з програми?", "Підтвердження", MessageBoxButtons.YesNo) == DialogResult.Yes)
            {
                Application.Exit();
            }
        }

        private void ResetControls(bool isCategory = false)
        {
            if (isCategory)
            {
                // Очищення полів категорії
                textBox_id_category.Text = string.Empty;
                textBox_title_category.Text = string.Empty;
                return;
            }

            // Очищення полів страви
            textBox_id.Text = string.Empty;
            textBox_title.Text = string.Empty;
            textBox_price.Text = string.Empty;
            textBox_spicy.Text = string.Empty;
            textBox_color.Text = string.Empty;
            textBox_category.Text = string.Empty;

            // Якщо є PictureBox для фото
            if (imageBox != null)
                imageBox.Image = null;
        }

    }
}
