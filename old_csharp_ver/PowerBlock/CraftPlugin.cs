using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PowerBlock
{
    class CraftPlugin
    {
        private string _Name;
        private string _Author;
        private string _Version;
        private API.PowerBlockPlugin _PluginMain;
        private API.Event.Listener.CraftListener _EventListener;

        public CraftPlugin(API.PowerBlockPlugin Plugin, API.Event.Listener.CraftListener Listener)
        {
            _PluginMain = Plugin;
            _EventListener = Listener;
            try
            {
                _Name = Plugin.Name();
                if (_Name.Length > 15)
                    _Name = _Name.Replace(_Name.Substring(16), "");
            }
            catch { }
            try
            {
                _Author = Plugin.Author();
                if (_Author.Length > 15)
                    _Author = _Author.Replace(_Author.Substring(16), "");
            }
            catch { }
            try
            {
                _Version = Plugin.Name();
                if (_Version.Length > 15)
                    _Version = _Version.Replace(_Version.Substring(16), "");
            }
            catch { }
        }

        public string Name { get { return _Name; } }
        public string Author { get { return _Author; } }
        public string Version { get { return _Version; } }
        public API.PowerBlockPlugin PluginMain { get { return _PluginMain; } }
        public API.Event.Listener.CraftListener EventListener { get { return _EventListener; } }
    }
}
