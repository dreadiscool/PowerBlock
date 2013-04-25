using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace PowerBlock
{
    class Writer : TextWriter
    {
        private TextWriter OldOut = Console.Out;

        public override void Write(string value)
        {
            OldOut.Write(value);
        }

        public override void WriteLine(string value)
        {
            OldOut.WriteLine("\r[" + DateTime.Now.Hour.ToString() + ":" + DateTime.Now.Minute.ToString() + ":" + DateTime.Now.Second + "] " + value);
            FileStream fs = new FileStream(CraftServer.Environment + CraftServer.DirStr + "server.log", FileMode.Append);
            StreamWriter sw = new StreamWriter(fs);
            sw.WriteLine("[" + DateTime.Now + "] " + value);
            sw.Flush();
            sw.Close();
            fs.Close();
            Write("> ");
        }

        public void WriteNoTimestamp(string value)
        {
            OldOut.WriteLine("\r" + value);
            OldOut.Write("> ");
        }

        public override Encoding Encoding
        {
            get { throw new Exception("The method or operation is not implemented."); }
        }
    }
}
