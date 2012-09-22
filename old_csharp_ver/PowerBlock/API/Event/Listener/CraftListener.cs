using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using PowerBlock.API.Event.Type;

namespace PowerBlock.API.Event.Listener
{
    public interface CraftListener
    {
        Response.CancelEvent HeartbeatSend(HeartbeatSendEvent e);
        Response.CancelEvent MapLoad(MapLoadEvent e);
        Response.CancelEvent PlayerBlockBreak(PlayerBlockBreakEvent e);
        Response.CancelEvent PlayerBlockBuild(PlayerBlockBuildEvent e);
        Response.CancelEvent PlayerChat(PlayerChatEvent e);
        Response.CommandResponse PlayerCommand(PlayerCommandEvent e);
        Response.DisplayMessage PlayerJoin(PlayerJoinEvent e);
        Response.CancelEvent PlayerLeave(PlayerLeaveEvent e);
        Response.CancelEvent PlayerTeleport(PlayerTeleportEvent e);
        Response.CancelEvent PlayerMove(PlayerMoveEvent e);
        Response.CancelEvent PlayerKick(PlayerKickEvent e);
        Response.CommandResponse ServerCommand(ServerCommandEvent e);
    }
}
