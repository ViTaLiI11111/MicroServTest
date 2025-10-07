using System;
using System.Windows.Forms;

namespace USRest_Admin
{
    internal class TimerVelocity
    {
        private Timer messageTimer;
        private Label labelMessage;

        public TimerVelocity(Label label)
        {
            labelMessage = label;
            InitializeMessageTimer();
        }

        private void InitializeMessageTimer()
        {
            messageTimer = new Timer();
            messageTimer.Interval = 5000; // 5 секунд
            messageTimer.Tick += MessageTimer_Tick;
        }

        private void MessageTimer_Tick(object sender, EventArgs e)
        {
            labelMessage.Text = string.Empty; // Сховати повідомлення
            messageTimer.Stop();
        }

        public void ShowTemporaryMessage(string message)
        {
            labelMessage.Text = message;
            messageTimer.Start();
        }
    }
}
