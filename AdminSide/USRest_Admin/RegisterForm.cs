using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using System.Windows.Forms;
using FireSharp.Config;
using FireSharp.Interfaces;
using FireSharp.Response;
using Newtonsoft.Json.Linq;

namespace USRest_Admin
{
    public partial class RegisterForm : Form
    {
        IFirebaseConfig config = new FirebaseConfig
        {
            AuthSecret = "j7NTtRceJ6UAv8A3JwotA9qzm2wGoyCV8eST2ucP",
            BasePath = "https://ukrrest-bc6a4-default-rtdb.firebaseio.com/"
        };

        IFirebaseClient client;

        public RegisterForm()
        {
            InitializeComponent();
            client = new FireSharp.FirebaseClient(config);
        }

        private void RegisterForm_Load(object sender, EventArgs e)
        {
        }

        private async void btnRegister_Click(object sender, EventArgs e)
        {
            if (!ValidateInputsLogin())
            {
                return;
            }
            try
            {
                FirebaseResponse resp = await client.GetTaskAsync("Admin/");
                var rawData = resp.Body;
                if (rawData != "null")
                {
                    MessageBox.Show("Користувач вже існує!");
                    return;
                }
                var data = new AdminData
                {
                    login = textBox_login.Text,
                    password = textBox_password.Text
                };

                SetResponse response = await client.SetTaskAsync("Admin/" + data.login, data);
                MessageBox.Show("Ви успішно зареєструвались!");
                LoginForm loginForm = new LoginForm();
                loginForm.FormClosed += (s, args) => this.Close();
                loginForm.Show();
                this.Hide();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Сталася помилка: " + ex.Message);
            }
        }

        private void btnBackToLogin_Click(object sender, EventArgs e)
        {
            LoginForm loginForm = new LoginForm();
            loginForm.FormClosed += (s, args) => this.Close();
            loginForm.Show();
            this.Hide();
        }

        private bool ValidateInputsLogin()
        {

            string loginPattern = @"^[a-z]+$";
            string passwordPattern = @"^[A-Za-z0-9]+$";

            if (!Regex.IsMatch(textBox_login.Text, loginPattern))
            {
                MessageBox.Show("Некоректний логін.");
                return false;
            }

            if (!Regex.IsMatch(textBox_password.Text, passwordPattern))
            {
                MessageBox.Show("Не коректний пароль.");
                return false;
            }

            return true;

        }
    }
}
