using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PowerBlock.API.Event.Type
{
    public class HeartbeatSendEvent
    {
        private string _URL = "";

        public HeartbeatSendEvent(string URL)
        {
            _URL = URL;
        }

        public string URL { get { return _URL; } }
    }
}
