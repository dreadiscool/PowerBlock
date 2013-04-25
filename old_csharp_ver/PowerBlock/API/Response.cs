using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PowerBlock.API
{
    public class Response
    {
        public enum CommandResponse { HANDLED, IGNORED } ;
        public enum CancelEvent { TRUE, FALSE } ;
        public enum DisplayMessage { TRUE, FALSE } ;
        public enum KickReason { SERVER_FULL, BANNED_ADDRESS, BANNED, TEMP_BANNED, NOT_WHITELISTED, WOM_FORBIDDEN, BAD_LOGIN, ADMIN_KICK, PLUGIN_KICK, BAD_PACKET_ID, AUTO_SPAM_DETECT, AUTO_GRIEF_DETECT, INTERNAL_SERVER_ERROR, UNSPECIFIED } ;
    }
}
