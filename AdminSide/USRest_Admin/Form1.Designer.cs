namespace USRest_Admin
{
    partial class Form1
    {
        /// <summary>
        /// Обязательная переменная конструктора.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Освободить все используемые ресурсы.
        /// </summary>
        /// <param name="disposing">истинно, если управляемый ресурс должен быть удален; иначе ложно.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Код, автоматически созданный конструктором форм Windows

        /// <summary>
        /// Требуемый метод для поддержки конструктора — не изменяйте 
        /// содержимое этого метода с помощью редактора кода.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Form1));
            this.textBox_id = new System.Windows.Forms.TextBox();
            this.textBox_title = new System.Windows.Forms.TextBox();
            this.textBox_price = new System.Windows.Forms.TextBox();
            this.textBox_spicy = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.insert = new System.Windows.Forms.Button();
            this.retrieve = new System.Windows.Forms.Button();
            this.delete = new System.Windows.Forms.Button();
            this.deleteAll = new System.Windows.Forms.Button();
            this.dataGridView1 = new System.Windows.Forms.DataGridView();
            this.exportToGridView = new System.Windows.Forms.Button();
            this.imageBox = new System.Windows.Forms.PictureBox();
            this.browse = new System.Windows.Forms.Button();
            this.textBox_category = new System.Windows.Forms.TextBox();
            this.label5 = new System.Windows.Forms.Label();
            this.labelMessage = new System.Windows.Forms.Label();
            this.clear = new System.Windows.Forms.Button();
            this.label6 = new System.Windows.Forms.Label();
            this.textBox_color = new System.Windows.Forms.TextBox();
            this.label7 = new System.Windows.Forms.Label();
            this.dataGridView2 = new System.Windows.Forms.DataGridView();
            this.insertCategory = new System.Windows.Forms.Button();
            this.retrieveCategory = new System.Windows.Forms.Button();
            this.label8 = new System.Windows.Forms.Label();
            this.label9 = new System.Windows.Forms.Label();
            this.textBox_id_category = new System.Windows.Forms.TextBox();
            this.textBox_title_category = new System.Windows.Forms.TextBox();
            this.deleteCategory = new System.Windows.Forms.Button();
            this.deleteAllCategories = new System.Windows.Forms.Button();
            this.exportToDataGridViewCategories = new System.Windows.Forms.Button();
            this.clearCategoryFields = new System.Windows.Forms.Button();
            this.UserDelete = new System.Windows.Forms.Button();
            ((System.ComponentModel.ISupportInitialize)(this.dataGridView1)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.imageBox)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.dataGridView2)).BeginInit();
            this.SuspendLayout();
            // 
            // textBox_id
            // 
            this.textBox_id.Location = new System.Drawing.Point(176, 356);
            this.textBox_id.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.textBox_id.Name = "textBox_id";
            this.textBox_id.Size = new System.Drawing.Size(148, 26);
            this.textBox_id.TabIndex = 0;
            // 
            // textBox_title
            // 
            this.textBox_title.Location = new System.Drawing.Point(176, 392);
            this.textBox_title.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.textBox_title.Name = "textBox_title";
            this.textBox_title.Size = new System.Drawing.Size(148, 26);
            this.textBox_title.TabIndex = 1;
            // 
            // textBox_price
            // 
            this.textBox_price.Location = new System.Drawing.Point(176, 426);
            this.textBox_price.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.textBox_price.Name = "textBox_price";
            this.textBox_price.Size = new System.Drawing.Size(148, 26);
            this.textBox_price.TabIndex = 2;
            // 
            // textBox_spicy
            // 
            this.textBox_spicy.Location = new System.Drawing.Point(176, 462);
            this.textBox_spicy.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.textBox_spicy.Name = "textBox_spicy";
            this.textBox_spicy.Size = new System.Drawing.Size(148, 26);
            this.textBox_spicy.TabIndex = 3;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.label1.Location = new System.Drawing.Point(21, 355);
            this.label1.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(40, 25);
            this.label1.TabIndex = 4;
            this.label1.Text = "ID:";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.label2.Location = new System.Drawing.Point(21, 391);
            this.label2.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(78, 25);
            this.label2.TabIndex = 5;
            this.label2.Text = "Назва:";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.label3.Location = new System.Drawing.Point(21, 427);
            this.label3.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(62, 25);
            this.label3.TabIndex = 6;
            this.label3.Text = "Ціна:";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.label4.Location = new System.Drawing.Point(21, 463);
            this.label4.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(116, 25);
            this.label4.TabIndex = 7;
            this.label4.Text = "Гострота:";
            // 
            // insert
            // 
            this.insert.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.insert.Location = new System.Drawing.Point(26, 611);
            this.insert.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.insert.Name = "insert";
            this.insert.Size = new System.Drawing.Size(259, 80);
            this.insert.TabIndex = 8;
            this.insert.Text = "Ввести/Оновити";
            this.insert.UseVisualStyleBackColor = true;
            this.insert.Click += new System.EventHandler(this.Insert_Click);
            // 
            // retrieve
            // 
            this.retrieve.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.retrieve.Location = new System.Drawing.Point(26, 697);
            this.retrieve.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.retrieve.Name = "retrieve";
            this.retrieve.Size = new System.Drawing.Size(259, 80);
            this.retrieve.TabIndex = 9;
            this.retrieve.Text = "Повернути";
            this.retrieve.UseVisualStyleBackColor = true;
            this.retrieve.Click += new System.EventHandler(this.Retrieve_Click);
            // 
            // delete
            // 
            this.delete.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.delete.Location = new System.Drawing.Point(27, 787);
            this.delete.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.delete.Name = "delete";
            this.delete.Size = new System.Drawing.Size(259, 80);
            this.delete.TabIndex = 11;
            this.delete.Text = "Видалити";
            this.delete.UseVisualStyleBackColor = true;
            this.delete.Click += new System.EventHandler(this.Delete_Click);
            // 
            // deleteAll
            // 
            this.deleteAll.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.deleteAll.Location = new System.Drawing.Point(26, 873);
            this.deleteAll.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.deleteAll.Name = "deleteAll";
            this.deleteAll.Size = new System.Drawing.Size(259, 80);
            this.deleteAll.TabIndex = 12;
            this.deleteAll.Text = "Видалити всі дані";
            this.deleteAll.UseVisualStyleBackColor = true;
            this.deleteAll.Click += new System.EventHandler(this.DeleteAll_Click);
            // 
            // dataGridView1
            // 
            this.dataGridView1.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dataGridView1.Location = new System.Drawing.Point(332, 354);
            this.dataGridView1.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.dataGridView1.Name = "dataGridView1";
            this.dataGridView1.RowHeadersWidth = 62;
            this.dataGridView1.Size = new System.Drawing.Size(1142, 253);
            this.dataGridView1.TabIndex = 13;
            // 
            // exportToGridView
            // 
            this.exportToGridView.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.exportToGridView.Location = new System.Drawing.Point(293, 697);
            this.exportToGridView.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.exportToGridView.Name = "exportToGridView";
            this.exportToGridView.Size = new System.Drawing.Size(259, 82);
            this.exportToGridView.TabIndex = 14;
            this.exportToGridView.Text = "Показати у DataGridView";
            this.exportToGridView.UseVisualStyleBackColor = true;
            this.exportToGridView.Click += new System.EventHandler(this.ExportToGridView_Click);
            // 
            // imageBox
            // 
            this.imageBox.BackColor = System.Drawing.SystemColors.AppWorkspace;
            this.imageBox.Location = new System.Drawing.Point(570, 613);
            this.imageBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.imageBox.Name = "imageBox";
            this.imageBox.Size = new System.Drawing.Size(493, 340);
            this.imageBox.TabIndex = 15;
            this.imageBox.TabStop = false;
            // 
            // browse
            // 
            this.browse.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.browse.Location = new System.Drawing.Point(294, 787);
            this.browse.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.browse.Name = "browse";
            this.browse.Size = new System.Drawing.Size(259, 80);
            this.browse.TabIndex = 16;
            this.browse.Text = "Вибрати зображення";
            this.browse.UseVisualStyleBackColor = true;
            this.browse.Click += new System.EventHandler(this.Browse_Click);
            // 
            // textBox_category
            // 
            this.textBox_category.Location = new System.Drawing.Point(176, 496);
            this.textBox_category.Name = "textBox_category";
            this.textBox_category.Size = new System.Drawing.Size(148, 26);
            this.textBox_category.TabIndex = 19;
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.label5.Location = new System.Drawing.Point(21, 497);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(118, 25);
            this.label5.TabIndex = 20;
            this.label5.Text = "Категорія:";
            // 
            // labelMessage
            // 
            this.labelMessage.AutoSize = true;
            this.labelMessage.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.labelMessage.Location = new System.Drawing.Point(342, 8);
            this.labelMessage.Name = "labelMessage";
            this.labelMessage.Size = new System.Drawing.Size(0, 25);
            this.labelMessage.TabIndex = 0;
            // 
            // clear
            // 
            this.clear.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.clear.Location = new System.Drawing.Point(1086, 613);
            this.clear.Name = "clear";
            this.clear.Size = new System.Drawing.Size(290, 72);
            this.clear.TabIndex = 21;
            this.clear.Text = "Очистити поля вводу";
            this.clear.UseVisualStyleBackColor = true;
            this.clear.Click += new System.EventHandler(this.Clear_Click);
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.label6.Location = new System.Drawing.Point(8, 531);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(165, 25);
            this.label6.TabIndex = 22;
            this.label6.Text = "Колір заднього";
            // 
            // textBox_color
            // 
            this.textBox_color.Location = new System.Drawing.Point(176, 555);
            this.textBox_color.Name = "textBox_color";
            this.textBox_color.Size = new System.Drawing.Size(148, 26);
            this.textBox_color.TabIndex = 23;
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.label7.Location = new System.Drawing.Point(8, 556);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(149, 25);
            this.label7.TabIndex = 24;
            this.label7.Text = "фону страви:";
            // 
            // dataGridView2
            // 
            this.dataGridView2.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dataGridView2.Location = new System.Drawing.Point(607, 62);
            this.dataGridView2.Name = "dataGridView2";
            this.dataGridView2.RowHeadersWidth = 62;
            this.dataGridView2.RowTemplate.Height = 28;
            this.dataGridView2.Size = new System.Drawing.Size(455, 253);
            this.dataGridView2.TabIndex = 25;
            // 
            // insertCategory
            // 
            this.insertCategory.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.insertCategory.Location = new System.Drawing.Point(13, 73);
            this.insertCategory.Name = "insertCategory";
            this.insertCategory.Size = new System.Drawing.Size(290, 80);
            this.insertCategory.TabIndex = 26;
            this.insertCategory.Text = "Ввести/Оновити";
            this.insertCategory.UseVisualStyleBackColor = true;
            this.insertCategory.Click += new System.EventHandler(this.InsertCategory_Click);
            // 
            // retrieveCategory
            // 
            this.retrieveCategory.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.retrieveCategory.Location = new System.Drawing.Point(309, 73);
            this.retrieveCategory.Name = "retrieveCategory";
            this.retrieveCategory.Size = new System.Drawing.Size(290, 80);
            this.retrieveCategory.TabIndex = 27;
            this.retrieveCategory.Text = "Повернути";
            this.retrieveCategory.UseVisualStyleBackColor = true;
            this.retrieveCategory.Click += new System.EventHandler(this.RetrieveCategory_Click);
            // 
            // label8
            // 
            this.label8.AutoSize = true;
            this.label8.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.label8.Location = new System.Drawing.Point(8, 8);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(138, 25);
            this.label8.TabIndex = 28;
            this.label8.Text = "ID категорії:";
            // 
            // label9
            // 
            this.label9.AutoSize = true;
            this.label9.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.label9.Location = new System.Drawing.Point(8, 41);
            this.label9.Name = "label9";
            this.label9.Size = new System.Drawing.Size(176, 25);
            this.label9.TabIndex = 29;
            this.label9.Text = "Назва категорії:";
            // 
            // textBox_id_category
            // 
            this.textBox_id_category.Location = new System.Drawing.Point(188, 7);
            this.textBox_id_category.Name = "textBox_id_category";
            this.textBox_id_category.Size = new System.Drawing.Size(148, 26);
            this.textBox_id_category.TabIndex = 30;
            // 
            // textBox_title_category
            // 
            this.textBox_title_category.Location = new System.Drawing.Point(188, 40);
            this.textBox_title_category.Name = "textBox_title_category";
            this.textBox_title_category.Size = new System.Drawing.Size(148, 26);
            this.textBox_title_category.TabIndex = 31;
            // 
            // deleteCategory
            // 
            this.deleteCategory.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.deleteCategory.Location = new System.Drawing.Point(13, 161);
            this.deleteCategory.Name = "deleteCategory";
            this.deleteCategory.Size = new System.Drawing.Size(290, 76);
            this.deleteCategory.TabIndex = 32;
            this.deleteCategory.Text = "Видалити";
            this.deleteCategory.UseVisualStyleBackColor = true;
            this.deleteCategory.Click += new System.EventHandler(this.DeleteCategory_Click);
            // 
            // deleteAllCategories
            // 
            this.deleteAllCategories.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.deleteAllCategories.Location = new System.Drawing.Point(13, 243);
            this.deleteAllCategories.Name = "deleteAllCategories";
            this.deleteAllCategories.Size = new System.Drawing.Size(290, 76);
            this.deleteAllCategories.TabIndex = 33;
            this.deleteAllCategories.Text = "Видалити всі дані";
            this.deleteAllCategories.UseVisualStyleBackColor = true;
            this.deleteAllCategories.Click += new System.EventHandler(this.DeleteAllCategory_Click);
            // 
            // exportToDataGridViewCategories
            // 
            this.exportToDataGridViewCategories.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.exportToDataGridViewCategories.Location = new System.Drawing.Point(309, 161);
            this.exportToDataGridViewCategories.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.exportToDataGridViewCategories.Name = "exportToDataGridViewCategories";
            this.exportToDataGridViewCategories.Size = new System.Drawing.Size(290, 72);
            this.exportToDataGridViewCategories.TabIndex = 34;
            this.exportToDataGridViewCategories.Text = "Показати у DataGridView";
            this.exportToDataGridViewCategories.UseVisualStyleBackColor = true;
            this.exportToDataGridViewCategories.Click += new System.EventHandler(this.ExportToDataGridViewCategory_Click);
            // 
            // clearCategoryFields
            // 
            this.clearCategoryFields.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.clearCategoryFields.Location = new System.Drawing.Point(1086, 243);
            this.clearCategoryFields.Name = "clearCategoryFields";
            this.clearCategoryFields.Size = new System.Drawing.Size(290, 72);
            this.clearCategoryFields.TabIndex = 35;
            this.clearCategoryFields.Text = "Очистити поля вводу";
            this.clearCategoryFields.UseVisualStyleBackColor = true;
            this.clearCategoryFields.Click += new System.EventHandler(this.ClearCategoryFields_Click);
            // 
            // UserDelete
            // 
            this.UserDelete.Font = new System.Drawing.Font("Microsoft Sans Serif", 14F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.UserDelete.Location = new System.Drawing.Point(1212, 813);
            this.UserDelete.Name = "UserDelete";
            this.UserDelete.Size = new System.Drawing.Size(262, 137);
            this.UserDelete.TabIndex = 36;
            this.UserDelete.Text = "Вийти та видалити користувача";
            this.UserDelete.UseVisualStyleBackColor = true;
            this.UserDelete.Click += new System.EventHandler(this.UserDelete_Click);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(9F, 20F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1485, 962);
            this.Controls.Add(this.UserDelete);
            this.Controls.Add(this.dataGridView2);
            this.Controls.Add(this.clearCategoryFields);
            this.Controls.Add(this.exportToDataGridViewCategories);
            this.Controls.Add(this.deleteAllCategories);
            this.Controls.Add(this.deleteCategory);
            this.Controls.Add(this.textBox_title_category);
            this.Controls.Add(this.textBox_id_category);
            this.Controls.Add(this.label9);
            this.Controls.Add(this.label8);
            this.Controls.Add(this.retrieveCategory);
            this.Controls.Add(this.insertCategory);
            this.Controls.Add(this.label7);
            this.Controls.Add(this.textBox_color);
            this.Controls.Add(this.label6);
            this.Controls.Add(this.clear);
            this.Controls.Add(this.labelMessage);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.textBox_category);
            this.Controls.Add(this.browse);
            this.Controls.Add(this.imageBox);
            this.Controls.Add(this.exportToGridView);
            this.Controls.Add(this.dataGridView1);
            this.Controls.Add(this.deleteAll);
            this.Controls.Add(this.delete);
            this.Controls.Add(this.retrieve);
            this.Controls.Add(this.insert);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.textBox_spicy);
            this.Controls.Add(this.textBox_price);
            this.Controls.Add(this.textBox_title);
            this.Controls.Add(this.textBox_id);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Name = "Form1";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Load += new System.EventHandler(this.Form1_Load);
            ((System.ComponentModel.ISupportInitialize)(this.dataGridView1)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.imageBox)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.dataGridView2)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TextBox textBox_id;
        private System.Windows.Forms.TextBox textBox_title;
        private System.Windows.Forms.TextBox textBox_price;
        private System.Windows.Forms.TextBox textBox_spicy;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Button insert;
        private System.Windows.Forms.Button retrieve;
        private System.Windows.Forms.Button delete;
        private System.Windows.Forms.Button deleteAll;
        private System.Windows.Forms.DataGridView dataGridView1;
        private System.Windows.Forms.Button exportToGridView;
        private System.Windows.Forms.PictureBox imageBox;
        private System.Windows.Forms.Button browse;
        private System.Windows.Forms.TextBox textBox_category;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Label labelMessage;
        private System.Windows.Forms.Button clear;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.TextBox textBox_color;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.DataGridView dataGridView2;
        private System.Windows.Forms.Button insertCategory;
        private System.Windows.Forms.Button retrieveCategory;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.Label label9;
        private System.Windows.Forms.TextBox textBox_id_category;
        private System.Windows.Forms.TextBox textBox_title_category;
        private System.Windows.Forms.Button deleteCategory;
        private System.Windows.Forms.Button deleteAllCategories;
        private System.Windows.Forms.Button exportToDataGridViewCategories;
        private System.Windows.Forms.Button clearCategoryFields;
        private System.Windows.Forms.Button UserDelete;
    }
}

