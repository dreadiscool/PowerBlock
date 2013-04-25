using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using PowerBlock.API.Event.Type;

namespace PowerBlock
{
    class CraftEvent
    {
        public static bool CraftServerCommandEvent(string FullCommand)
        {
            string Command = "";
            List<string> ListArgs = new List<string>();
            string[] Split = FullCommand.Split(' ');
            bool GotCommand = false;
            foreach (string s in Split)
            {
                if (GotCommand)
                    ListArgs.Add(s);
                else
                {
                    Command = s;
                    GotCommand = true;
                }
            }
            bool Handled = false;
            ServerCommandEvent PluginEvent = new ServerCommandEvent(Command, ListArgs.ToArray());
            foreach (CraftPlugin cp in CraftServer.Plugins)
            {
                try
                {
                    if (cp.EventListener.ServerCommand(PluginEvent) == API.Response.CommandResponse.HANDLED)
                        Handled = true;
                }
                catch (Exception PluginException)
                {
                    Console.WriteLine("Nag  " + cp.Author + " about " + cp.Name + " being broken!\n" + PluginException.ToString());
                }
            }
            return Handled;
        }

        public static bool CraftHeartbeatEvent(string URL)
        {
            bool Cancelled = false;
            HeartbeatSendEvent PluginEvent = new HeartbeatSendEvent(URL);
            foreach (CraftPlugin cp in CraftServer.Plugins)
            {
                try
                {
                    if (cp.EventListener.HeartbeatSend(PluginEvent) == API.Response.CancelEvent.TRUE)
                        Cancelled = true;
                }
                catch (Exception PluginException)
                {
                    Console.WriteLine("Nag  " + cp.Author + " about " + cp.Name + " being broken!\n" + PluginException.ToString());
                }
            }
            return Cancelled;
        }

        public static bool CraftPlayerJoinEvent(CraftPlayer cp)
        {
            bool DisplayMessage = true;
            PlayerJoinEvent PluginEvent = new PlayerJoinEvent(new API.Player(cp.Username));
            foreach (CraftPlugin cplugin in CraftServer.Plugins)
            {
                try
                {
                    if (cplugin.EventListener.PlayerJoin(PluginEvent) == API.Response.DisplayMessage.FALSE)
                        DisplayMessage = false;
                }
                catch (Exception PluginException)
                {
                    Console.WriteLine("Nag  " + cplugin.Author + " about " + cplugin.Name + " being broken!\n" + PluginException.ToString());
                }
            }
            return DisplayMessage;
        }

        public static bool CraftPlayerChatEvent(CraftPlayer cp, string Message)
        {
            bool Cancelled = false;
            PlayerChatEvent PluginEvent = new PlayerChatEvent(new API.Player(cp.Username), Message);
            foreach (CraftPlugin cplugin in CraftServer.Plugins)
            {
                try
                {
                    if (cplugin.EventListener.PlayerChat(PluginEvent) == API.Response.CancelEvent.TRUE)
                        Cancelled = true;
                }
                catch (Exception PluginException)
                {
                    Console.WriteLine("Nag  " + cplugin.Author + " about " + cplugin.Name + " being broken!\n" + PluginException.ToString());
                }
            }
            return Cancelled;
        }

        public static bool CraftPlayerMoveEvent(CraftPlayer cp, API.Point3D Position, byte pitch, byte yaw)
        {
            bool Cancelled = false;
            PlayerMoveEvent PluginEvent = new PlayerMoveEvent(new API.Player(cp.Username), Position, pitch, yaw);
            foreach (CraftPlugin cplugin in CraftServer.Plugins)
            {
                try
                {
                    if (cplugin.EventListener.PlayerMove(PluginEvent) == API.Response.CancelEvent.TRUE)
                        Cancelled = true;
                }
                catch (Exception PluginException)
                {
                    Console.WriteLine("Nag  " + cplugin.Author + " about " + cplugin.Name + " being broken!\n" + PluginException.ToString());
                }
            }
            return Cancelled;
        }

        public static bool CraftPlayerBuildEvent()
        {
            bool Cancelled = false;
            PlayerBlockBuildEvent PluginEvent = new PlayerBlockBuildEvent();
            foreach (CraftPlugin cplugin in CraftServer.Plugins)
            {
                try
                {
                    if (cplugin.EventListener.PlayerBlockBuild(PluginEvent) == API.Response.CancelEvent.TRUE)
                        Cancelled = true;
                }
                catch (Exception PluginException)
                {
                    Console.WriteLine("Nag  " + cplugin.Author + " about " + cplugin.Name + " being broken!\n" + PluginException.ToString());
                }
            }
            return Cancelled;
        }

        public static bool CraftPlayerBreakEvent()
        {
            bool Cancelled = false;
            PlayerBlockBreakEvent PluginEvent = new PlayerBlockBreakEvent();
            foreach (CraftPlugin cplugin in CraftServer.Plugins)
            {
                try
                {
                    if (cplugin.EventListener.PlayerBlockBreak(PluginEvent) == API.Response.CancelEvent.TRUE)
                        Cancelled = true;
                }
                catch (Exception PluginException)
                {
                    Console.WriteLine("Nag  " + cplugin.Author + " about " + cplugin.Name + " being broken!\n" + PluginException.ToString());
                }
            }
            return Cancelled;
        }

        public static bool CraftPlayerKickEvent(CraftPlayer cp, string Message, API.Response.KickReason Reason)
        {
            bool Cancelled = false;
            PlayerKickEvent PluginEvent = new PlayerKickEvent(new API.Player(cp.Username), Message, Reason);
            foreach (CraftPlugin cplugin in CraftServer.Plugins)
            {
                try
                {
                    if (cplugin.EventListener.PlayerKick(PluginEvent) == API.Response.CancelEvent.TRUE)
                        Cancelled = true;
                }
                catch (Exception PluginException)
                {
                    Console.WriteLine("Nag  " + cplugin.Author + " about " + cplugin.Name + " being broken!\n" + PluginException.ToString());
                }
            }
            return Cancelled;
        }

        public static void CraftPlayerQuitEvent(CraftPlayer cp)
        {
            PlayerLeaveEvent PluginEvent = new PlayerLeaveEvent(new API.Player(cp.Username));
            foreach (CraftPlugin cplugin in CraftServer.Plugins)
            {
                try
                {
                    cplugin.EventListener.PlayerLeave(PluginEvent);
                }
                catch (Exception PluginException)
                {
                    Console.WriteLine("Nag  " + cplugin.Author + " about " + cplugin.Name + " being broken!\n" + PluginException.ToString());
                }
            }
        }

        public static bool CraftPlayerCommandEvent(CraftPlayer cp, string Command)
        {
            bool Handled = false;
            PlayerCommandEvent pce = new PlayerCommandEvent(new API.Player(cp.Username), Command);
            foreach (CraftPlugin cplugin in CraftServer.Plugins)
            {
                try
                {
                    if (cplugin.EventListener.PlayerCommand(pce) == API.Response.CommandResponse.HANDLED)
                        Handled = true;
                }
                catch (Exception PluginException)
                {
                    Console.WriteLine("Nag  " + cplugin.Author + " about " + cplugin.Name + " being broken!\n" + PluginException.ToString());
                }
            }
            return Handled;
        }
    }
}
