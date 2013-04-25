using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PowerBlock.API.Event.Type
{
    public class ServerCommandEvent
    {
        private string _Command = "";
        private string[] _Args = null;

        public ServerCommandEvent(string PluginCommand, string[] PluginArgs)
        {
            _Command = PluginCommand;
            _Args = PluginArgs;
        }

        public string Command { get { return _Command; } }
        public string[] Args { get { return _Args; } }
    }
}
