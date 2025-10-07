using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Cache;
using System.Net.NetworkInformation;
using System.Net.Sockets;
using System.Runtime.InteropServices;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Windows.Forms;
using FirebaseAdmin;
using Newtonsoft.Json.Linq;

using FireSharp.Config;
using FireSharp.Exceptions;
using FireSharp.Interfaces;
using FireSharp.Response;
using static System.Net.Mime.MediaTypeNames;
using static System.Windows.Forms.VisualStyles.VisualStyleElement;
using static Google.Apis.Requests.BatchRequest;
using Image = System.Drawing.Image;
using TextBox = System.Windows.Forms.TextBox;
using System.Diagnostics;

namespace USRest_Admin
{
    public partial class Form1 : Form
    {

        DataTable dt = new DataTable();
        DataTable dtCategory = new DataTable();

        IFirebaseConfig config = new FirebaseConfig()
        {
            AuthSecret = "j7NTtRceJ6UAv8A3JwotA9qzm2wGoyCV8eST2ucP",
            BasePath = "https://ukrrest-bc6a4-default-rtdb.firebaseio.com/"

        };

        IFirebaseClient client;

        public Form1()
        {
            InitializeComponent();
            timerVelocity = new TimerVelocity(labelMessage);
        }

        private TimerVelocity timerVelocity;

        private void Form1_Load(object sender, EventArgs e)
        {
            CheckInternetConnection();
            
            client = new FireSharp.FirebaseClient(config);

            dt = new DataTable();
            dt.Columns.Add("Id");
            dt.Columns.Add("Назва");
            dt.Columns.Add("Ціна");
            dt.Columns.Add("Гострота");
            dt.Columns.Add("Категорія");
            dt.Columns.Add("Задній фон");
            dt.Columns.Add("Зображення", typeof(Image));
            dataGridView1.DataSource = dt;

            dtCategory = new DataTable();
            dtCategory.Columns.Add("Id категорії");
            dtCategory.Columns.Add("Назва категорії");
            dataGridView2.DataSource = dtCategory;
        }

        private void CheckInternetConnection()
        {
            if (!IsInternetAvailable())
            {
                MessageBox.Show("Немає з'єднання з Інтернетом.", "Помилка", MessageBoxButtons.OK, MessageBoxIcon.Error); 
            }
        }

        private bool IsInternetAvailable()
        {
            try
            {
                using (var ping = new Ping())
                {
                    var reply = ping.Send("8.8.8.8"); // Google DNS
                    return reply.Status == IPStatus.Success;
                }
            }
            catch (Exception)
            {
                return false;
            }
        }

        private void ResetControls(bool isCategory = false)
        {
            if (isCategory)
            {
                ResetTextBoxes(textBox_id_category, textBox_title_category);
            }
            else
            {
                imageBox.Image = null;
                ResetTextBoxes(textBox_id, textBox_title, textBox_price, textBox_spicy, textBox_category, textBox_color);
            }
        }

        private void ResetTextBoxes(params TextBox[] textBoxes)
        {
            foreach (var textBox in textBoxes)
            {
                textBox.Text = null;
            }
        }

        private void ButtonReset_Click(object sender, EventArgs e)
        {
            ResetControls();
        }

        private void ButtonResetCategory_Click(object sender, EventArgs e)
        {
            ResetControls(true);
        }


        private bool ValidateInputs()
        {
            string idPattern = @"^\d+$";
            string titlePattern = @"^[A-Za-zА-Яа-яІіЇїЄєҐґ\s'’]+(?:-[A-Za-zА-Яа-яІіЇїЄєҐґ\s'’]+)*$";// Тільки букви та знак "-"
            string pricePattern = @"^\d+$";  // Тільки цілі числа
            string pepperPattern = @"^\d+$";
            string colorPattern = @"^#[A-Za-z0-9]+$";  // Латинські букви та цифри, але на початку має бути символ "#"
            string categoryPattern = @"^\d+$";  // Тільки цілі числа

            if (!Regex.IsMatch(textBox_id.Text, idPattern))
            {
                timerVelocity.ShowTemporaryMessage("Некоректний ідентифікатор (ID).");
                return false;
            }

            if (!Regex.IsMatch(textBox_title.Text, titlePattern))
            {
                timerVelocity.ShowTemporaryMessage("Поле 'Назва' не відповідає вимогам.");
                return false;
            }

            if (!Regex.IsMatch(textBox_price.Text, pricePattern))
            {
                timerVelocity.ShowTemporaryMessage("Некоректне значення ціни.");
                return false;
            }

            if (!Regex.IsMatch(textBox_spicy.Text, pepperPattern))
            {
                timerVelocity.ShowTemporaryMessage("Поле 'Гострота' не відповідає вимогам.");
                return false;
            }

            if (!Regex.IsMatch(textBox_color.Text, colorPattern))
            {
                timerVelocity.ShowTemporaryMessage("Поле 'Колір' не відповідає вимогам.");
                return false;
            }

            if (!Regex.IsMatch(textBox_category.Text, categoryPattern))
            {
                timerVelocity.ShowTemporaryMessage("Некоректна категорія.");
                return false;
            }

            return true;
            
        }

        private bool AreAllFieldsEmpty(bool isCategory)
        {
            if (isCategory)
            {
                return string.IsNullOrEmpty(textBox_id_category.Text) ||
                       string.IsNullOrEmpty(textBox_title_category.Text);
            }
            else
            {
                return string.IsNullOrEmpty(textBox_id.Text) ||
                       string.IsNullOrEmpty(textBox_title.Text) ||
                       string.IsNullOrEmpty(textBox_price.Text) ||
                       string.IsNullOrEmpty(textBox_spicy.Text) ||
                       string.IsNullOrEmpty(textBox_color.Text) ||
                       string.IsNullOrEmpty(textBox_category.Text);
            }
        }

        private Bitmap CreatePlaceholderImage(string text)
        {
            Bitmap bmp = new Bitmap(60, 50); // Розмір зображення
            using (Graphics g = Graphics.FromImage(bmp))
            {
                g.Clear(Color.White);
                using (Font font = new Font("Arial", 8))
                {
                    g.DrawString(text, font, Brushes.Black, new PointF(10, 20));
                }
            }
            return bmp;
        }

        /*///////////////////////////////////////////////////////////
                        ЕЛЕМЕНТИ ІНТЕРФЕЙСА ПРОГРАМИ
        ///////////////////////////////////////////////////////////*/


        /*///////////////////////////////////////////////////////////
                                     МЕНЮ
        ///////////////////////////////////////////////////////////*/
        private async void Insert_Click(object sender, EventArgs e)
        {
            if (AreAllFieldsEmpty(false))
            {
                timerVelocity.ShowTemporaryMessage("Поля вводу не заповнені!!!/Відсутня інформація для оновлення.");
                return;
            }
            if (imageBox.Image == null)
            {
                timerVelocity.ShowTemporaryMessage("Зображення не вибрано!!!");
                return;
            }
            if (!ValidateInputs())
            {
                return;
            }
            try
            {
                MemoryStream ms = new MemoryStream();
                imageBox.Image.Save(ms, ImageFormat.Png);
                byte[] a = ms.GetBuffer();
                string output = Convert.ToBase64String(a);

                var data = new DishData
                {
                    id = Convert.ToInt32(textBox_id.Text),
                    title = textBox_title.Text,
                    price = textBox_price.Text,
                    pepper = textBox_spicy.Text,
                    color = textBox_color.Text,
                    category = Convert.ToInt32(textBox_category.Text),
                    img = output
                };

                SetResponse response = await client.SetTaskAsync("Dishes/" + data.id, data);
                DishData result = response.ResultAs<DishData>();
                ResetControls();

                timerVelocity.ShowTemporaryMessage("Дані введені: /Дані оновлені у: " + result.id);
            }
            catch (Exception ex)
            {
                timerVelocity.ShowTemporaryMessage("Сталася помилка: " + ex.Message);
            }
        }

        private async void Retrieve_Click(object sender, EventArgs e)
        {
            if (!string.IsNullOrWhiteSpace(textBox_id.Text))
            {
                if (string.IsNullOrWhiteSpace(textBox_id.Text) || !Regex.IsMatch(textBox_id.Text, @"^\d+$"))
                {
                    timerVelocity.ShowTemporaryMessage("Некоректний ідентифікатор (ID).");
                    return;
                }
                FirebaseResponse response = await client.GetTaskAsync("Dishes/" + textBox_id.Text);
                if (response == null || response.Body == "null")
                {
                    ResetControls();
                    timerVelocity.ShowTemporaryMessage("Дані не знайдені.");
                    return;
                }
                DishData obj = response.ResultAs<DishData>();
                if (obj != null)
                {
                    textBox_id.Text = Convert.ToString(obj.id);
                    textBox_title.Text = obj.title;
                    textBox_price.Text = obj.price;
                    textBox_spicy.Text = obj.pepper;
                    textBox_color.Text = obj.color;
                    textBox_category.Text = Convert.ToString(obj.category);

                    if (obj.img != null)
                    {
                        byte[] b = Convert.FromBase64String(obj.img);
                        MemoryStream ms = new MemoryStream();
                        ms.Write(b, 0, Convert.ToInt32(b.Length));
                        Bitmap bm = new Bitmap(ms, false);
                        imageBox.Image = bm;
                    }
                    else
                    {
                        imageBox.Image = null;
                    }
                    timerVelocity.ShowTemporaryMessage("Дані повернені успішно.");
                }
                else
                {
                    ResetControls();
                    timerVelocity.ShowTemporaryMessage("Дані не знайдені.");
                }
            }
            else
            {
                timerVelocity.ShowTemporaryMessage("Введіть id страви.");
            }
        }

        private async void Delete_Click(object sender, EventArgs e)
        {
            FirebaseResponse getResponse = await client.GetTaskAsync("Dishes/" + textBox_id.Text);
            if (getResponse.Body == "null")
            {
                timerVelocity.ShowTemporaryMessage("Дані не знайдені у базі.");
                return;
            }
            else
            {
                DialogResult result = MessageBox.Show("Бажаєте видалити дані цієї страви?", "Підтвердження", MessageBoxButtons.YesNo);

                if (result == DialogResult.Yes)
                {
                    if (string.IsNullOrWhiteSpace(textBox_id.Text) || !Regex.IsMatch(textBox_id.Text, @"^\d+$"))
                    {
                        timerVelocity.ShowTemporaryMessage("Некоректний ідентифікатор (ID) або він відстуній.");
                        return;
                    }
                    try
                    {
                        FirebaseResponse deleteResponse = await client.DeleteTaskAsync("Dishes/" + textBox_id.Text);
                        ResetControls();
                        timerVelocity.ShowTemporaryMessage("Дані видалені.");
                    }
                    catch (Exception ex)
                    {
                        timerVelocity.ShowTemporaryMessage("Сталася помилка: " + ex.Message);
                    }
                }
                else if (result == DialogResult.No) { }
            }
        }

        private async void DeleteAll_Click(object sender, EventArgs e)
        {
            FirebaseResponse getResponse = await client.GetTaskAsync("Dishes/");
            if (getResponse.Body == "null")
            {
                timerVelocity.ShowTemporaryMessage("Дані не знайдені у базі.");
                return;
            }
            else
            {
                DialogResult result = MessageBox.Show("Бажаєте видалити всі дані з бази даних?", "Підтвердження", MessageBoxButtons.YesNo);

                if (result == DialogResult.Yes)
                {
                    FirebaseResponse response = await client.DeleteTaskAsync("Dishes/");
                    timerVelocity.ShowTemporaryMessage("Всі дані видалено!");
                    ResetControls();
                }
                else if (result == DialogResult.No)
                {
                    timerVelocity.ShowTemporaryMessage("Будьте уважними)");
                }
            }
        }

        private async void ExportToGridView_Click(object sender, EventArgs e)
        {
            FirebaseResponse resp = await client.GetTaskAsync("Dishes/");
            if (resp == null || resp.Body == "null")
            {
                timerVelocity.ShowTemporaryMessage("У базі даних відсутня інформація.");
                dt.Rows.Clear();
                return;
            }
            else
            {
                await Export();
            }
        }

        private async Task Export()
        {
            dt.Rows.Clear();
            try
            {
                FirebaseResponse resp = await client.GetTaskAsync("Dishes/");
                var rawData = resp.Body; // Отримуємо сирі дані у вигляді JSON-строки

                var jToken = Newtonsoft.Json.Linq.JToken.Parse(rawData);

                List<DishData> dishes = new List<DishData>();

                if (jToken is Newtonsoft.Json.Linq.JObject jObject)
                {
                    foreach (var property in jObject.Properties())
                    {
                        var dish = property.Value.ToObject<DishData>();
                        dishes.Add(dish);
                    }
                }
                else if (jToken is Newtonsoft.Json.Linq.JArray jArray)
                {
                    dishes = jArray.ToObject<List<DishData>>();
                }

                if (dishes == null || dishes.Count == 0)
                {
                    timerVelocity.ShowTemporaryMessage("Немає даних для завантаження.");
                    return;
                }

                foreach (var dish in dishes)
                {
                    if (dish == null)
                        continue;

                    DataRow row = dt.NewRow();
                    row["id"] = dish.id;
                    row["Назва"] = dish.title;
                    row["Ціна"] = dish.price;
                    row["Гострота"] = dish.pepper;
                    row["Задній фон"] = dish.color;
                    row["Категорія"] = dish.category;

                    if (!string.IsNullOrEmpty(dish.img))
                    {
                        byte[] b = Convert.FromBase64String(dish.img);
                        MemoryStream ms = new MemoryStream();
                        ms.Write(b, 0, Convert.ToInt32(b.Length));
                        Bitmap bm = new Bitmap(ms, false);
                        row["Зображення"] = bm;
                    }
                    else
                    {
                        row["Зображення"] = CreatePlaceholderImage("Відсутнє зображення.");
                    }
                    dt.Rows.Add(row);
                }

                timerVelocity.ShowTemporaryMessage("Готово!");
            }
            catch (Exception ex)
            {
                timerVelocity.ShowTemporaryMessage("Сталася помилка: " + ex.Message);
            }
        }


        private void Browse_Click(object sender, EventArgs e)
        {
            OpenFileDialog ofd = new OpenFileDialog();
            ofd.Title = "Виберіть зображення";
            ofd.Filter = "Файли зображень типу(*.png) | *png";
            if (ofd.ShowDialog() == DialogResult.OK)
            {
                Image img = new Bitmap(ofd.FileName);
                imageBox.Image = img.GetThumbnailImage(330, 220, null, new IntPtr());
            }
        }

        private void Clear_Click(object sender, EventArgs e)
        {
            ResetControls();
        }

        /*////////////////////////////////////////////
                          Категорії
        ////////////////////////////////////////////*/
        private bool ValidateInputsCategory() { 
        
            string idPatternCategory = @"^\d+$";
            string titlePatternCategory = @"^[A-Za-zА-Яа-яІіЇїЄєҐґ\s]+(?:-[A-Za-zА-Яа-яІіЇїЄєҐґ\s]+)*$";// Тільки букви та знак "-"

            if (!Regex.IsMatch(textBox_id_category.Text, idPatternCategory))
            {
                timerVelocity.ShowTemporaryMessage("Некоректний ідентифікатор (ID).");
                return false;
            }

            if (!Regex.IsMatch(textBox_title_category.Text, titlePatternCategory))
            {
                timerVelocity.ShowTemporaryMessage("Поле 'Назва' не відповідає вимогам.");
                return false;
            }

            return true;
            
        }

        private async void InsertCategory_Click(object sender, EventArgs e)
        {
            if (AreAllFieldsEmpty(true))
            {
                timerVelocity.ShowTemporaryMessage("Поля вводу не заповнені!!!/Відсутня інформація для оновлення.");
                return;
            }
            if (!ValidateInputsCategory())
            {
                return;
            }
            try
            {
                var data_category = new CategoryData
                {
                    id = Convert.ToInt32(textBox_id_category.Text),
                    title = textBox_title_category.Text
                };

                SetResponse response = await client.SetTaskAsync("Categories/" + data_category.id, data_category);
                CategoryData result = response.ResultAs<CategoryData>();
                ResetControls(true);

                timerVelocity.ShowTemporaryMessage("Дані введені: /Дані оновлені у: " + result.id);
            }
            catch (Exception ex)
            {
                timerVelocity.ShowTemporaryMessage("Сталася помилка: " + ex.Message);
            }
        }

        private async void RetrieveCategory_Click(object sender, EventArgs e)
        {
            if (!string.IsNullOrWhiteSpace(textBox_id_category.Text))
            {
                if (string.IsNullOrWhiteSpace(textBox_id_category.Text) || !Regex.IsMatch(textBox_id_category.Text, @"^\d+$"))
                {
                    timerVelocity.ShowTemporaryMessage("Некоректний ідентифікатор (ID).");
                    return;
                }
                FirebaseResponse response = await client.GetTaskAsync("Categories/" + textBox_id_category.Text);
                if (response == null || response.Body == "null")
                {
                    ResetControls(true);
                    timerVelocity.ShowTemporaryMessage("Дані не знайдені.");
                    return;
                }
                CategoryData obj = response.ResultAs<CategoryData>();
                if (obj != null)
                {
                    textBox_id_category.Text = Convert.ToString(obj.id);
                    textBox_title_category.Text = obj.title;
                    timerVelocity.ShowTemporaryMessage("Дані повернені успішно.");
                }
                else
                {
                    ResetControls(true);
                    timerVelocity.ShowTemporaryMessage("Дані не знайдені.");
                }
            }
            else
            {
                timerVelocity.ShowTemporaryMessage("Введіть id категорії.");
            }
        }

        private async void DeleteCategory_Click(object sender, EventArgs e)
        {
            FirebaseResponse getResponse = await client.GetTaskAsync("Categories/" + textBox_id_category.Text);

            if (getResponse == null || getResponse.Body == "null")
            {
                timerVelocity.ShowTemporaryMessage("Дані не знайдені у базі.");
                return;
            }
            else
            {
                DialogResult result = MessageBox.Show("Бажаєте видалити дані цієї категорії?", "Підтвердження", MessageBoxButtons.YesNo);

                if (result == DialogResult.Yes)
                {
                    if (string.IsNullOrWhiteSpace(textBox_id_category.Text) || !Regex.IsMatch(textBox_id_category.Text, @"^\d+$"))
                    {
                        timerVelocity.ShowTemporaryMessage("Некоректний ідентифікатор (ID) або він відстуній.");
                        return;
                    }
                    try
                    {
                        FirebaseResponse deleteResponse = await client.DeleteTaskAsync("Categories/" + textBox_id_category.Text);
                        ResetControls(true);
                        timerVelocity.ShowTemporaryMessage("Дані видалені.");
                    }
                    catch (Exception ex)
                    {
                        timerVelocity.ShowTemporaryMessage("Сталася помилка: " + ex.Message);
                    }
                }
                else if (result == DialogResult.No) { }
            }
        }

        private async void DeleteAllCategory_Click(object sender, EventArgs e)
        {
            FirebaseResponse getResponse = await client.GetTaskAsync("Categories/");
            if (getResponse.Body == "null")
            {
                timerVelocity.ShowTemporaryMessage("Дані не знайдені у базі.");
                return;
            }
            else
            {
                DialogResult result = MessageBox.Show("Бажаєте видалити всі дані категорій?", "Підтвердження", MessageBoxButtons.YesNo);

                if (result == DialogResult.Yes)
                {

                    FirebaseResponse response = await client.DeleteTaskAsync("Categories/");
                    timerVelocity.ShowTemporaryMessage("Всі дані видалено!");
                    ResetControls(true);

                }
                else if (result == DialogResult.No)
                {
                    timerVelocity.ShowTemporaryMessage("Будьте уважними)");
                }
            }
        }

        private async void ExportToDataGridViewCategory_Click(object sender, EventArgs e)
        {
            FirebaseResponse resp = await client.GetTaskAsync("Categories/");
            if (resp == null || resp.Body == "null")
            {
                timerVelocity.ShowTemporaryMessage("У базі даних відсутня інформація.");
                dtCategory.Rows.Clear();
                return;
            }
            else
            {
                await Export_category();
            }
        }

        private async Task Export_category()
        {
            dtCategory.Rows.Clear();
            try
            {
                FirebaseResponse resp = await client.GetTaskAsync("Categories/");
                List<CategoryData> categories = resp.ResultAs<List<CategoryData>>();

                if (categories == null || categories.Count == 0)
                {
                    timerVelocity.ShowTemporaryMessage("Немає даних для завантаження.");
                    return;
                }
                foreach (var ctgr in categories)
                {
                    if (ctgr == null)
                        continue;

                    DataRow row = dtCategory.NewRow();
                    row["Id категорії"] = ctgr.id;
                    row["Назва категорії"] = ctgr.title;

                    dtCategory.Rows.Add(row);
                }
                timerVelocity.ShowTemporaryMessage("Готово!");
            }
            catch (Exception ex)
            {
                timerVelocity.ShowTemporaryMessage("Сталася помилка: " + ex.Message);
            }

        }

        private void ClearCategoryFields_Click(object sender, EventArgs e)
        {
            ResetControls(true);
        }

        private async void UserDelete_Click(object sender, EventArgs e)
        {
            DialogResult result = MessageBox.Show("Бажаєте видалити всі дані категорій?", "Підтвердження", MessageBoxButtons.YesNo);

            if (result == DialogResult.Yes)
            {
                FirebaseResponse response = await client.DeleteTaskAsync("Admin/");
                LoginForm loginForm = new LoginForm();
                loginForm.Show();
                this.Close();
            }
        }
    }
}