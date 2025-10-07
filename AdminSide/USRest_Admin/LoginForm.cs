using System;
using System.Windows.Forms;
using FireSharp.Config;
using FireSharp.Interfaces;
using FireSharp.Response;

namespace USRest_Admin
{
    public partial class LoginForm : Form
    {
        IFirebaseConfig config = new FirebaseConfig
        {
            AuthSecret = "j7NTtRceJ6UAv8A3JwotA9qzm2wGoyCV8eST2ucP",
            BasePath = "https://ukrrest-bc6a4-default-rtdb.firebaseio.com/"
        };

        IFirebaseClient client;

        public LoginForm()
        {
            InitializeComponent();
            client = new FireSharp.FirebaseClient(config);
        }

        private void LoginForm_Load(object sender, EventArgs e)
        {}

        private void btnRegister_Click(object sender, EventArgs e)
        {
            RegisterForm registerForm = new RegisterForm();
            registerForm.FormClosed += (s, args) => this.Close();
            registerForm.Show();
            this.Hide();
        }


        private async void btnLogin_Click(object sender, EventArgs e)
        {
            if (AreAllFieldsEmpty())
            {
                MessageBox.Show("Поля вводу не заповнені");
                return;
            }

            try
            {
                FirebaseResponse response = await client.GetTaskAsync("Admin/" + textBox_login.Text);

                if (response == null || response.Body == "null")
                {
                    MessageBox.Show("Такого користувача не існує!");
                    return;
                }

                AdminData existingUser = response.ResultAs<AdminData>();

                if (existingUser.password == textBox_password.Text)
                {
                    Form1 mainForm = new Form1();
                    mainForm.FormClosed += (s, args) => this.Close();
                    this.Hide();  
                    mainForm.Show();  
                }
                else
                {
                    MessageBox.Show("Неправильний пароль!");
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Сталася помилка: " + ex.Message);
            }
        }


        private bool AreAllFieldsEmpty()
        {
            return string.IsNullOrEmpty(textBox_login.Text) ||
                    string.IsNullOrEmpty(textBox_password.Text);
        }
    }
}
