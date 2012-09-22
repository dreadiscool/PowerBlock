using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using PowerBlock.API.Event.Type;

namespace PowerBlock.API.Event.Listener
{
    public class Listener : CraftListener
    {
        public virtual Response.CancelEvent HeartbeatSend(HeartbeatSendEvent e) { return Response.CancelEvent.FALSE; }
        public virtual Response.CancelEvent MapLoad(MapLoadEvent e) { return Response.CancelEvent.FALSE; }
        public virtual Response.CancelEvent PlayerBlockBreak(PlayerBlockBreakEvent e) { return Response.CancelEvent.FALSE; }
        public virtual Response.CancelEvent PlayerBlockBuild(PlayerBlockBuildEvent e) { return Response.CancelEvent.FALSE; }
        public virtual Response.CancelEvent PlayerChat(PlayerChatEvent e) { return Response.CancelEvent.FALSE; }
        public virtual Response.CommandResponse PlayerCommand(PlayerCommandEvent e) { return Response.CommandResponse.IGNORED; }
        public virtual Response.DisplayMessage PlayerJoin(PlayerJoinEvent e) { return Response.DisplayMessage.TRUE; }
        public virtual Response.CancelEvent PlayerLeave(PlayerLeaveEvent e) { return Response.CancelEvent.FALSE; }
        public virtual Response.CancelEvent PlayerMove(PlayerMoveEvent e) { return Response.CancelEvent.FALSE; }
        public virtual Response.CancelEvent PlayerTeleport(PlayerTeleportEvent e) { return Response.CancelEvent.FALSE; }
        public virtual Response.CancelEvent PlayerKick(PlayerKickEvent e) { return Response.CancelEvent.FALSE; }
        public virtual Response.CommandResponse ServerCommand(ServerCommandEvent e) { return Response.CommandResponse.IGNORED; }
    }
}
