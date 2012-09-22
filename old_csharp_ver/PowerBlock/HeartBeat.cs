using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;
using System.Threading;

namespace PowerBlock
{
    class HeartBeat
    {
        private static bool ShownOnce = false;
        private string Request = "http://www.minecraft.net/heartbeat.jsp?port=PORT&max=MAX&name=SERVER-NAME&public=True&version=7&salt=SALT&users=USERS";

        public HeartBeat()
        {
            Request = Request.Replace("PORT", CraftServer.Port.ToString());
            Request = Request.Replace("MAX", CraftServer.MaxPlayers.ToString());
            Request = Request.Replace("SERVER-NAME", CraftServer.Name);
            Request = Request.Replace("SALT", CraftServer.Salt);
            Request = Request.Replace("USERS", CraftServer.Players.Length.ToString());
            CraftEvent.CraftHeartbeatEvent(Request);
            ThreadPool.QueueUserWorkItem(new WaitCallback(AsyncRequest));
        }

        public void AsyncRequest(object o)
        {
            try
            {
                WebClient wc = new WebClient();
                string Response = wc.DownloadString(Request);
                if (Response.StartsWith("http://www.minecraft.net") == false)
                {
                    Console.WriteLine("Failed to send heartbeat! Is minecraft.net down?");
                }
                else
                {
                    FileStream fs = new FileStream(CraftServer.Environment + CraftServer.DirStr + "external-url.txt", FileMode.Create);
                    StreamWriter sw = new StreamWriter(fs);
                    sw.WriteLine(Response);
                    sw.Flush();
                    sw.Close();
                    fs.Close();
                    if (ShownOnce == false)
                    {
                        Console.WriteLine("Saved HeartBeat URL to external-url.txt!");
                        ShownOnce = true;
                    }
                }
            }
            catch (Exception HeartBeatException)
            {
                Console.WriteLine("Failed to send heartbeat! Is minecraft.net down?\n\t" + HeartBeatException.Message);
            }
        }
    }
}
