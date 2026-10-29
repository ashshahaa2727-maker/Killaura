package globals.shared.proto;


import rockstar.client.i18n.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;

public final class Packets {
   public static final int MAX_MESSAGE_LENGTH = 200;
   public static final int MAX_PRIVATE_MESSAGE_LENGTH = 500;

   public static final class InternalType0006 implements Packet {
      private final String nickname;
      private final String anarchy;
      private final String server;
      private final String hash;

      public InternalType0006(String localValue1, String localValue2, String localValue3, String localValue4) {
         this.nickname = localValue1;
         this.anarchy = localValue2;
         this.server = localValue3;
         this.hash = localValue4;
      }

      @Override
      public String type() {
         return "admin_shutdown";
      }

      @Override
      public final String toString() {
         return "InternalType0006[nickname=" + this.nickname() + ", anarchy=" + this.anarchy() + ", server=" + this.server() + ", hash=" + this.hash() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.nickname());
         result = 31 * result + java.util.Objects.hashCode(this.anarchy());
         result = 31 * result + java.util.Objects.hashCode(this.server());
         result = 31 * result + java.util.Objects.hashCode(this.hash());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0006 other = (Packets.InternalType0006) localValue1;
         return java.util.Objects.equals(this.nickname(), other.nickname())
            && java.util.Objects.equals(this.anarchy(), other.anarchy())
            && java.util.Objects.equals(this.server(), other.server())
            && java.util.Objects.equals(this.hash(), other.hash());
      }

      public String nickname() {
         return this.nickname;
      }

      public String anarchy() {
         return this.anarchy;
      }

      public String server() {
         return this.server;
      }

      public String hash() {
         return this.hash;
      }
   }

   public static final class InternalType0007 implements Packet {
      private final String toUsername;

      public InternalType0007(String localValue1) {
         this.toUsername = localValue1;
      }

      @Override
      public String type() {
         return "friend_request_send";
      }

      @Override
      public final String toString() {
         return "InternalType0007[toUsername=" + this.toUsername() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.toUsername());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0007 other = (Packets.InternalType0007) localValue1;
         return java.util.Objects.equals(this.toUsername(), other.toUsername());
      }

      public String toUsername() {
         return this.toUsername;
      }
   }

   public static final class InternalType0015 implements Packet {
      private final String badge;
      private final String nickStyle;

      public InternalType0015(String localValue1, String localValue2) {
         this.badge = localValue1;
         this.nickStyle = localValue2;
      }

      @Override
      public String type() {
         return "self_cosmetics";
      }

      @Override
      public final String toString() {
         return "InternalType0015[badge=" + this.badge() + ", nickStyle=" + this.nickStyle() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.badge());
         result = 31 * result + java.util.Objects.hashCode(this.nickStyle());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0015 other = (Packets.InternalType0015) localValue1;
         return java.util.Objects.equals(this.badge(), other.badge())
            && java.util.Objects.equals(this.nickStyle(), other.nickStyle());
      }

      public String badge() {
         return this.badge;
      }

      public String nickStyle() {
         return this.nickStyle;
      }
   }

   public static final class InternalType0017 implements Packet {
      private final List<Packets.InternalType0144> messages;
      private final long beforeId;
      private final boolean more;

      public InternalType0017(List<Packets.InternalType0144> localValue1, long localValue2, boolean localValue4) {
         this.messages = localValue1;
         this.beforeId = localValue2;
         this.more = localValue4;
      }

      @Override
      public String type() {
         return "chat_history";
      }

      @Override
      public final String toString() {
         return "InternalType0017[messages=" + this.messages() + ", beforeId=" + this.beforeId() + ", more=" + this.more() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.messages());
         result = 31 * result + java.util.Objects.hashCode(this.beforeId());
         result = 31 * result + java.util.Objects.hashCode(this.more());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0017 other = (Packets.InternalType0017) localValue1;
         return java.util.Objects.equals(this.messages(), other.messages())
            && java.util.Objects.equals(this.beforeId(), other.beforeId())
            && java.util.Objects.equals(this.more(), other.more());
      }

      public List<Packets.InternalType0144> messages() {
         return this.messages;
      }

      public long beforeId() {
         return this.beforeId;
      }

      public boolean more() {
         return this.more;
      }
   }

   public static final class InternalType0018 {
      private final String username;
      private final String role;
      private final String activity;
      private final Packets.InternalType0031 gameInfo;
      private final String client;
      private final String visibility;
      private final String badge;
      private final String nickStyle;
      private final long lastSeen;

      public InternalType0018(
         String localValue1, String localValue2, String localValue3, Packets.InternalType0031 localValue4, String localValue5, String localValue6, String localValue7, String localValue8, long localValue9
      ) {
         this.username = localValue1;
         this.role = localValue2;
         this.activity = localValue3;
         this.gameInfo = localValue4;
         this.client = localValue5;
         this.visibility = localValue6;
         this.badge = localValue7;
         this.nickStyle = localValue8;
         this.lastSeen = localValue9;
      }

      @Override
      public final String toString() {
         return "InternalType0018[username=" + this.username() + ", role=" + this.role() + ", activity=" + this.activity() + ", gameInfo=" + this.gameInfo() + ", client=" + this.client() + ", visibility=" + this.visibility() + ", badge=" + this.badge() + ", nickStyle=" + this.nickStyle() + ", lastSeen=" + this.lastSeen() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.username());
         result = 31 * result + java.util.Objects.hashCode(this.role());
         result = 31 * result + java.util.Objects.hashCode(this.activity());
         result = 31 * result + java.util.Objects.hashCode(this.gameInfo());
         result = 31 * result + java.util.Objects.hashCode(this.client());
         result = 31 * result + java.util.Objects.hashCode(this.visibility());
         result = 31 * result + java.util.Objects.hashCode(this.badge());
         result = 31 * result + java.util.Objects.hashCode(this.nickStyle());
         result = 31 * result + java.util.Objects.hashCode(this.lastSeen());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0018 other = (Packets.InternalType0018) localValue1;
         return java.util.Objects.equals(this.username(), other.username())
            && java.util.Objects.equals(this.role(), other.role())
            && java.util.Objects.equals(this.activity(), other.activity())
            && java.util.Objects.equals(this.gameInfo(), other.gameInfo())
            && java.util.Objects.equals(this.client(), other.client())
            && java.util.Objects.equals(this.visibility(), other.visibility())
            && java.util.Objects.equals(this.badge(), other.badge())
            && java.util.Objects.equals(this.nickStyle(), other.nickStyle())
            && java.util.Objects.equals(this.lastSeen(), other.lastSeen());
      }

      public String username() {
         return this.username;
      }

      public String role() {
         return this.role;
      }

      public String activity() {
         return this.activity;
      }

      public Packets.InternalType0031 gameInfo() {
         return this.gameInfo;
      }

      public String client() {
         return this.client;
      }

      public String visibility() {
         return this.visibility;
      }

      public String badge() {
         return this.badge;
      }

      public String nickStyle() {
         return this.nickStyle;
      }

      public long lastSeen() {
         return this.lastSeen;
      }
   }

   public static final class InternalType0019 implements Packet {
      private final String name;
      private final String data;

      public InternalType0019(String localValue1, String localValue2) {
         this.name = localValue1;
         this.data = localValue2;
      }

      @Override
      public String type() {
         return "swing_preset";
      }

      @Override
      public final String toString() {
         return "InternalType0019[name=" + this.name() + ", data=" + this.data() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.name());
         result = 31 * result + java.util.Objects.hashCode(this.data());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0019 other = (Packets.InternalType0019) localValue1;
         return java.util.Objects.equals(this.name(), other.name())
            && java.util.Objects.equals(this.data(), other.data());
      }

      public String name() {
         return this.name;
      }

      public String data() {
         return this.data;
      }
   }

   public static final class InternalType0031 {
      private final String nickname;
      private final String anarchy;
      private final String server;
      private final String ip;
      private final String hash;
      private final String visibility;
      private final String clientName;

      public InternalType0031(String localValue1, String localValue2, String localValue3, String localValue4, String localValue5, String localValue6, String localValue7) {
         this.nickname = localValue1;
         this.anarchy = localValue2;
         this.server = localValue3;
         this.ip = localValue4;
         this.hash = localValue5;
         this.visibility = localValue6;
         this.clientName = localValue7;
      }

      @Override
      public final String toString() {
         return "InternalType0031[nickname=" + this.nickname() + ", anarchy=" + this.anarchy() + ", server=" + this.server() + ", ip=" + this.ip() + ", hash=" + this.hash() + ", visibility=" + this.visibility() + ", clientName=" + this.clientName() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.nickname());
         result = 31 * result + java.util.Objects.hashCode(this.anarchy());
         result = 31 * result + java.util.Objects.hashCode(this.server());
         result = 31 * result + java.util.Objects.hashCode(this.ip());
         result = 31 * result + java.util.Objects.hashCode(this.hash());
         result = 31 * result + java.util.Objects.hashCode(this.visibility());
         result = 31 * result + java.util.Objects.hashCode(this.clientName());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0031 other = (Packets.InternalType0031) localValue1;
         return java.util.Objects.equals(this.nickname(), other.nickname())
            && java.util.Objects.equals(this.anarchy(), other.anarchy())
            && java.util.Objects.equals(this.server(), other.server())
            && java.util.Objects.equals(this.ip(), other.ip())
            && java.util.Objects.equals(this.hash(), other.hash())
            && java.util.Objects.equals(this.visibility(), other.visibility())
            && java.util.Objects.equals(this.clientName(), other.clientName());
      }

      public String nickname() {
         return this.nickname;
      }

      public String anarchy() {
         return this.anarchy;
      }

      public String server() {
         return this.server;
      }

      public String ip() {
         return this.ip;
      }

      public String hash() {
         return this.hash;
      }

      public String visibility() {
         return this.visibility;
      }

      public String clientName() {
         return this.clientName;
      }
   }

   public static final class InternalType0032 implements Packet {
      private final JsonObject payload;

      public InternalType0032(JsonObject localValue1) {
         this.payload = localValue1;
      }

      @Override
      public String type() {
         return "autofarm_inventory";
      }

      @Override
      public final String toString() {
         return "InternalType0032[payload=" + this.payload() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.payload());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0032 other = (Packets.InternalType0032) localValue1;
         return java.util.Objects.equals(this.payload(), other.payload());
      }

      public JsonObject payload() {
         return this.payload;
      }
   }

   public static final class InternalType0033 implements Packet {
      private final String name;
      private final String language;
      private final String blob;
      private final List<String> libraries;

      public InternalType0033(String localValue1, String localValue2, String localValue3, List<String> localValue4) {
         this.name = localValue1;
         this.language = localValue2;
         this.blob = localValue3;
         this.libraries = localValue4;
      }

      @Override
      public String type() {
         return "script_protected";
      }

      @Override
      public final String toString() {
         return "InternalType0033[name=" + this.name() + ", language=" + this.language() + ", blob=" + this.blob() + ", libraries=" + this.libraries() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.name());
         result = 31 * result + java.util.Objects.hashCode(this.language());
         result = 31 * result + java.util.Objects.hashCode(this.blob());
         result = 31 * result + java.util.Objects.hashCode(this.libraries());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0033 other = (Packets.InternalType0033) localValue1;
         return java.util.Objects.equals(this.name(), other.name())
            && java.util.Objects.equals(this.language(), other.language())
            && java.util.Objects.equals(this.blob(), other.blob())
            && java.util.Objects.equals(this.libraries(), other.libraries());
      }

      public String name() {
         return this.name;
      }

      public String language() {
         return this.language;
      }

      public String blob() {
         return this.blob;
      }

      public List<String> libraries() {
         return this.libraries;
      }
   }

   public static final class InternalType0036 implements Packet {
      private final String name;

      public InternalType0036(String localValue1) {
         this.name = localValue1;
      }

      @Override
      public String type() {
         return "script_delete";
      }

      @Override
      public final String toString() {
         return "InternalType0036[name=" + this.name() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.name());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0036 other = (Packets.InternalType0036) localValue1;
         return java.util.Objects.equals(this.name(), other.name());
      }

      public String name() {
         return this.name;
      }
   }

   public static final class InternalType0037 implements Packet {
      private final String name;
      private final String data;

      public InternalType0037(String localValue1, String localValue2) {
         this.name = localValue1;
         this.data = localValue2;
      }

      @Override
      public String type() {
         return "invbuilder_preset";
      }

      @Override
      public final String toString() {
         return "InternalType0037[name=" + this.name() + ", data=" + this.data() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.name());
         result = 31 * result + java.util.Objects.hashCode(this.data());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0037 other = (Packets.InternalType0037) localValue1;
         return java.util.Objects.equals(this.name(), other.name())
            && java.util.Objects.equals(this.data(), other.data());
      }

      public String name() {
         return this.name;
      }

      public String data() {
         return this.data;
      }
   }

   public static final class InternalType0038 implements Packet {
      private final String nickname;
      private final String anarchy;
      private final String server;
      private final String hash;

      public InternalType0038(String localValue1, String localValue2, String localValue3, String localValue4) {
         this.nickname = localValue1;
         this.anarchy = localValue2;
         this.server = localValue3;
         this.hash = localValue4;
      }

      @Override
      public String type() {
         return "admin_drop";
      }

      @Override
      public final String toString() {
         return "InternalType0038[nickname=" + this.nickname() + ", anarchy=" + this.anarchy() + ", server=" + this.server() + ", hash=" + this.hash() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.nickname());
         result = 31 * result + java.util.Objects.hashCode(this.anarchy());
         result = 31 * result + java.util.Objects.hashCode(this.server());
         result = 31 * result + java.util.Objects.hashCode(this.hash());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0038 other = (Packets.InternalType0038) localValue1;
         return java.util.Objects.equals(this.nickname(), other.nickname())
            && java.util.Objects.equals(this.anarchy(), other.anarchy())
            && java.util.Objects.equals(this.server(), other.server())
            && java.util.Objects.equals(this.hash(), other.hash());
      }

      public String nickname() {
         return this.nickname;
      }

      public String anarchy() {
         return this.anarchy;
      }

      public String server() {
         return this.server;
      }

      public String hash() {
         return this.hash;
      }
   }

   public static final class InternalType0041 implements Packet {
      private final Packets.InternalType0018 author;
      private final Packets.InternalType0068 position;
      private final Packets.InternalType0068 direction;
      private final String anarchy;
      private final String server;
      private final String hash;

      public InternalType0041(
         Packets.InternalType0018 localValue1, Packets.InternalType0068 localValue2, Packets.InternalType0068 localValue3, String localValue4, String localValue5, String localValue6
      ) {
         this.author = localValue1;
         this.position = localValue2;
         this.direction = localValue3;
         this.anarchy = localValue4;
         this.server = localValue5;
         this.hash = localValue6;
      }

      @Override
      public String type() {
         return "snowball_throw";
      }

      @Override
      public final String toString() {
         return "InternalType0041[author=" + this.author() + ", position=" + this.position() + ", direction=" + this.direction() + ", anarchy=" + this.anarchy() + ", server=" + this.server() + ", hash=" + this.hash() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.author());
         result = 31 * result + java.util.Objects.hashCode(this.position());
         result = 31 * result + java.util.Objects.hashCode(this.direction());
         result = 31 * result + java.util.Objects.hashCode(this.anarchy());
         result = 31 * result + java.util.Objects.hashCode(this.server());
         result = 31 * result + java.util.Objects.hashCode(this.hash());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0041 other = (Packets.InternalType0041) localValue1;
         return java.util.Objects.equals(this.author(), other.author())
            && java.util.Objects.equals(this.position(), other.position())
            && java.util.Objects.equals(this.direction(), other.direction())
            && java.util.Objects.equals(this.anarchy(), other.anarchy())
            && java.util.Objects.equals(this.server(), other.server())
            && java.util.Objects.equals(this.hash(), other.hash());
      }

      public Packets.InternalType0018 author() {
         return this.author;
      }

      public Packets.InternalType0068 position() {
         return this.position;
      }

      public Packets.InternalType0068 direction() {
         return this.direction;
      }

      public String anarchy() {
         return this.anarchy;
      }

      public String server() {
         return this.server;
      }

      public String hash() {
         return this.hash;
      }
   }

   public static final class InternalType0047 {
      private final String username;
      private final String role;
      private final String badge;
      private final String nickStyle;
      private final boolean online;
      private final long lastSeen;

      public InternalType0047(String localValue1, String localValue2, String localValue3, String localValue4, boolean localValue5, long localValue6) {
         this.username = localValue1;
         this.role = localValue2;
         this.badge = localValue3;
         this.nickStyle = localValue4;
         this.online = localValue5;
         this.lastSeen = localValue6;
      }

      @Override
      public final String toString() {
         return "InternalType0047[username=" + this.username() + ", role=" + this.role() + ", badge=" + this.badge() + ", nickStyle=" + this.nickStyle() + ", online=" + this.online() + ", lastSeen=" + this.lastSeen() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.username());
         result = 31 * result + java.util.Objects.hashCode(this.role());
         result = 31 * result + java.util.Objects.hashCode(this.badge());
         result = 31 * result + java.util.Objects.hashCode(this.nickStyle());
         result = 31 * result + java.util.Objects.hashCode(this.online());
         result = 31 * result + java.util.Objects.hashCode(this.lastSeen());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0047 other = (Packets.InternalType0047) localValue1;
         return java.util.Objects.equals(this.username(), other.username())
            && java.util.Objects.equals(this.role(), other.role())
            && java.util.Objects.equals(this.badge(), other.badge())
            && java.util.Objects.equals(this.nickStyle(), other.nickStyle())
            && java.util.Objects.equals(this.online(), other.online())
            && java.util.Objects.equals(this.lastSeen(), other.lastSeen());
      }

      public String username() {
         return this.username;
      }

      public String role() {
         return this.role;
      }

      public String badge() {
         return this.badge;
      }

      public String nickStyle() {
         return this.nickStyle;
      }

      public boolean online() {
         return this.online;
      }

      public long lastSeen() {
         return this.lastSeen;
      }
   }

   public static final class InternalType0059 implements Packet {
      private final JsonObject data;

      public InternalType0059(JsonObject localValue1) {
         this.data = localValue1;
      }

      @Override
      public String type() {
         return "client_data";
      }

      @Override
      public final String toString() {
         return "InternalType0059[data=" + this.data() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.data());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0059 other = (Packets.InternalType0059) localValue1;
         return java.util.Objects.equals(this.data(), other.data());
      }

      public JsonObject data() {
         return this.data;
      }
   }

   public static final class InternalType0060 implements Packet {
      private final String server;
      private final String ip;
      private final String host;
      private final String nickname;
      private final String clientName;

      public InternalType0060(String localValue1, String localValue2, String localValue3, String localValue4, String localValue5) {
         this.server = localValue1;
         this.ip = localValue2;
         this.host = localValue3;
         this.nickname = localValue4;
         this.clientName = localValue5;
      }

      @Override
      public String type() {
         return "client_join";
      }

      @Override
      public final String toString() {
         return "InternalType0060[server=" + this.server() + ", ip=" + this.ip() + ", host=" + this.host() + ", nickname=" + this.nickname() + ", clientName=" + this.clientName() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.server());
         result = 31 * result + java.util.Objects.hashCode(this.ip());
         result = 31 * result + java.util.Objects.hashCode(this.host());
         result = 31 * result + java.util.Objects.hashCode(this.nickname());
         result = 31 * result + java.util.Objects.hashCode(this.clientName());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0060 other = (Packets.InternalType0060) localValue1;
         return java.util.Objects.equals(this.server(), other.server())
            && java.util.Objects.equals(this.ip(), other.ip())
            && java.util.Objects.equals(this.host(), other.host())
            && java.util.Objects.equals(this.nickname(), other.nickname())
            && java.util.Objects.equals(this.clientName(), other.clientName());
      }

      public String server() {
         return this.server;
      }

      public String ip() {
         return this.ip;
      }

      public String host() {
         return this.host;
      }

      public String nickname() {
         return this.nickname;
      }

      public String clientName() {
         return this.clientName;
      }
   }

   public static final class InternalType0061 implements Packet {
      private final String username;
      private final long seconds;
      private final String reason;

      public InternalType0061(String localValue1, long localValue2, String localValue4) {
         this.username = localValue1;
         this.seconds = localValue2;
         this.reason = localValue4;
      }

      @Override
      public String type() {
         return "mod_mute";
      }

      @Override
      public final String toString() {
         return "InternalType0061[username=" + this.username() + ", seconds=" + this.seconds() + ", reason=" + this.reason() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.username());
         result = 31 * result + java.util.Objects.hashCode(this.seconds());
         result = 31 * result + java.util.Objects.hashCode(this.reason());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0061 other = (Packets.InternalType0061) localValue1;
         return java.util.Objects.equals(this.username(), other.username())
            && java.util.Objects.equals(this.seconds(), other.seconds())
            && java.util.Objects.equals(this.reason(), other.reason());
      }

      public String username() {
         return this.username;
      }

      public long seconds() {
         return this.seconds;
      }

      public String reason() {
         return this.reason;
      }
   }

   public static final class InternalType0063 implements Packet {
      private final String text;

      public InternalType0063(String localValue1) {
         this.text = localValue1;
      }

      @Override
      public String type() {
         return "auth_result";
      }

      @Override
      public final String toString() {
         return "InternalType0063[text=" + this.text() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.text());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0063 other = (Packets.InternalType0063) localValue1;
         return java.util.Objects.equals(this.text(), other.text());
      }

      public String text() {
         return this.text;
      }
   }

   public static final class InternalType0064 implements Packet {
      private final String name;
      private final Packets.InternalType0068 vector;
      private final String anarchy;
      private final String server;
      private final String hash;
      private final long livingTime;
      private final int color;

      public InternalType0064(String localValue1, Packets.InternalType0068 localValue2, String localValue3, String localValue4, String localValue5, long localValue6, int localValue8) {
         this.name = localValue1;
         this.vector = localValue2;
         this.anarchy = localValue3;
         this.server = localValue4;
         this.hash = localValue5;
         this.livingTime = localValue6;
         this.color = localValue8;
      }

      @Override
      public String type() {
         return "create_way_send";
      }

      @Override
      public final String toString() {
         return "InternalType0064[name=" + this.name() + ", vector=" + this.vector() + ", anarchy=" + this.anarchy() + ", server=" + this.server() + ", hash=" + this.hash() + ", livingTime=" + this.livingTime() + ", color=" + this.color() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.name());
         result = 31 * result + java.util.Objects.hashCode(this.vector());
         result = 31 * result + java.util.Objects.hashCode(this.anarchy());
         result = 31 * result + java.util.Objects.hashCode(this.server());
         result = 31 * result + java.util.Objects.hashCode(this.hash());
         result = 31 * result + java.util.Objects.hashCode(this.livingTime());
         result = 31 * result + java.util.Objects.hashCode(this.color());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0064 other = (Packets.InternalType0064) localValue1;
         return java.util.Objects.equals(this.name(), other.name())
            && java.util.Objects.equals(this.vector(), other.vector())
            && java.util.Objects.equals(this.anarchy(), other.anarchy())
            && java.util.Objects.equals(this.server(), other.server())
            && java.util.Objects.equals(this.hash(), other.hash())
            && java.util.Objects.equals(this.livingTime(), other.livingTime())
            && java.util.Objects.equals(this.color(), other.color());
      }

      public String name() {
         return this.name;
      }

      public Packets.InternalType0068 vector() {
         return this.vector;
      }

      public String anarchy() {
         return this.anarchy;
      }

      public String server() {
         return this.server;
      }

      public String hash() {
         return this.hash;
      }

      public long livingTime() {
         return this.livingTime;
      }

      public int color() {
         return this.color;
      }
   }

   public static final class InternalType0067 implements Packet {
      private final String withUsername;
      private final long beforeId;

      public InternalType0067(String localValue1, long localValue2) {
         this.withUsername = localValue1;
         this.beforeId = localValue2;
      }

      @Override
      public String type() {
         return "private_history_request";
      }

      @Override
      public final String toString() {
         return "InternalType0067[withUsername=" + this.withUsername() + ", beforeId=" + this.beforeId() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.withUsername());
         result = 31 * result + java.util.Objects.hashCode(this.beforeId());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0067 other = (Packets.InternalType0067) localValue1;
         return java.util.Objects.equals(this.withUsername(), other.withUsername())
            && java.util.Objects.equals(this.beforeId(), other.beforeId());
      }

      public String withUsername() {
         return this.withUsername;
      }

      public long beforeId() {
         return this.beforeId;
      }
   }

   public static final class InternalType0068 {
      private final double x;
      private final double y;
      private final double z;

      public InternalType0068(double localValue1, double localValue3, double localValue5) {
         this.x = localValue1;
         this.y = localValue3;
         this.z = localValue5;
      }

      @Override
      public final String toString() {
         return "InternalType0068[x=" + this.x() + ", y=" + this.y() + ", z=" + this.z() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.x());
         result = 31 * result + java.util.Objects.hashCode(this.y());
         result = 31 * result + java.util.Objects.hashCode(this.z());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0068 other = (Packets.InternalType0068) localValue1;
         return java.util.Objects.equals(this.x(), other.x())
            && java.util.Objects.equals(this.y(), other.y())
            && java.util.Objects.equals(this.z(), other.z());
      }

      public double x() {
         return this.x;
      }

      public double y() {
         return this.y;
      }

      public double z() {
         return this.z;
      }
   }

   public static final class InternalType0071 implements Packet {
      private final String name;
      private final String data;
      private final String toUsername;

      public InternalType0071(String localValue1, String localValue2, String localValue3) {
         this.name = localValue1;
         this.data = localValue2;
         this.toUsername = localValue3;
      }

      @Override
      public String type() {
         return "share_invbuilder_send";
      }

      @Override
      public final String toString() {
         return "InternalType0071[name=" + this.name() + ", data=" + this.data() + ", toUsername=" + this.toUsername() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.name());
         result = 31 * result + java.util.Objects.hashCode(this.data());
         result = 31 * result + java.util.Objects.hashCode(this.toUsername());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0071 other = (Packets.InternalType0071) localValue1;
         return java.util.Objects.equals(this.name(), other.name())
            && java.util.Objects.equals(this.data(), other.data())
            && java.util.Objects.equals(this.toUsername(), other.toUsername());
      }

      public String name() {
         return this.name;
      }

      public String data() {
         return this.data;
      }

      public String toUsername() {
         return this.toUsername;
      }
   }

   public static final class InternalType0072 implements Packet {
      private final long configId;
      private final String toUsername;

      public InternalType0072(long localValue1, String localValue3) {
         this.configId = localValue1;
         this.toUsername = localValue3;
      }

      @Override
      public String type() {
         return "share_config_send";
      }

      @Override
      public final String toString() {
         return "InternalType0072[configId=" + this.configId() + ", toUsername=" + this.toUsername() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.configId());
         result = 31 * result + java.util.Objects.hashCode(this.toUsername());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0072 other = (Packets.InternalType0072) localValue1;
         return java.util.Objects.equals(this.configId(), other.configId())
            && java.util.Objects.equals(this.toUsername(), other.toUsername());
      }

      public long configId() {
         return this.configId;
      }

      public String toUsername() {
         return this.toUsername;
      }
   }

   public static final class InternalType0073 implements Packet {
      private final String message;

      public InternalType0073(String localValue1) {
         if (localValue1 != null && localValue1.length() > 200) {
            throw new IllegalArgumentException("Message cannot be longer than 200 characters");
         } else {
            this.message = localValue1;
         }
      }

      @Override
      public String type() {
         return "message_send";
      }

      @Override
      public final String toString() {
         return "InternalType0073[message=" + this.message() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.message());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0073 other = (Packets.InternalType0073) localValue1;
         return java.util.Objects.equals(this.message(), other.message());
      }

      public String message() {
         return this.message;
      }
   }

   public static final class InternalType0074 implements Packet {
      private final String nickname;
      private final String anarchy;
      private final String server;
      private final String hash;

      public InternalType0074(String localValue1, String localValue2, String localValue3, String localValue4) {
         this.nickname = localValue1;
         this.anarchy = localValue2;
         this.server = localValue3;
         this.hash = localValue4;
      }

      @Override
      public String type() {
         return "admin_freeze";
      }

      @Override
      public final String toString() {
         return "InternalType0074[nickname=" + this.nickname() + ", anarchy=" + this.anarchy() + ", server=" + this.server() + ", hash=" + this.hash() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.nickname());
         result = 31 * result + java.util.Objects.hashCode(this.anarchy());
         result = 31 * result + java.util.Objects.hashCode(this.server());
         result = 31 * result + java.util.Objects.hashCode(this.hash());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0074 other = (Packets.InternalType0074) localValue1;
         return java.util.Objects.equals(this.nickname(), other.nickname())
            && java.util.Objects.equals(this.anarchy(), other.anarchy())
            && java.util.Objects.equals(this.server(), other.server())
            && java.util.Objects.equals(this.hash(), other.hash());
      }

      public String nickname() {
         return this.nickname;
      }

      public String anarchy() {
         return this.anarchy;
      }

      public String server() {
         return this.server;
      }

      public String hash() {
         return this.hash;
      }
   }

   public static final class InternalType0078 implements Packet {
      private final long id;

      public InternalType0078(long localValue1) {
         this.id = localValue1;
      }

      @Override
      public String type() {
         return "config_duplicate";
      }

      @Override
      public final String toString() {
         return "InternalType0078[id=" + this.id() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.id());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0078 other = (Packets.InternalType0078) localValue1;
         return java.util.Objects.equals(this.id(), other.id());
      }

      public long id() {
         return this.id;
      }
   }

   public static final class InternalType0079 implements Packet {
      private final String name;

      public InternalType0079(String localValue1) {
         this.name = localValue1;
      }

      @Override
      public String type() {
         return "script_remove";
      }

      @Override
      public final String toString() {
         return "InternalType0079[name=" + this.name() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.name());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0079 other = (Packets.InternalType0079) localValue1;
         return java.util.Objects.equals(this.name(), other.name());
      }

      public String name() {
         return this.name;
      }
   }

   public static final class InternalType0080 implements Packet {
      private final String from;
      private final String to;

      public InternalType0080(String localValue1, String localValue2) {
         this.from = localValue1;
         this.to = localValue2;
      }

      @Override
      public String type() {
         return "script_rename";
      }

      @Override
      public final String toString() {
         return "InternalType0080[from=" + this.from() + ", to=" + this.to() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.from());
         result = 31 * result + java.util.Objects.hashCode(this.to());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0080 other = (Packets.InternalType0080) localValue1;
         return java.util.Objects.equals(this.from(), other.from())
            && java.util.Objects.equals(this.to(), other.to());
      }

      public String from() {
         return this.from;
      }

      public String to() {
         return this.to;
      }
   }

   public static final class InternalType0083 implements Packet {
      private final String username;

      public InternalType0083(String localValue1) {
         this.username = localValue1;
      }

      @Override
      public String type() {
         return "profile_request";
      }

      @Override
      public final String toString() {
         return "InternalType0083[username=" + this.username() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.username());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0083 other = (Packets.InternalType0083) localValue1;
         return java.util.Objects.equals(this.username(), other.username());
      }

      public String username() {
         return this.username;
      }
   }

   public static final class InternalType0085 implements Packet {
      private final String nickname;
      private final String anarchy;
      private final String server;
      private final String hash;
      private final int slot;
      private final int button;
      private final String action;

      public InternalType0085(String localValue1, String localValue2, String localValue3, String localValue4, int localValue5, int localValue6, String localValue7) {
         this.nickname = localValue1;
         this.anarchy = localValue2;
         this.server = localValue3;
         this.hash = localValue4;
         this.slot = localValue5;
         this.button = localValue6;
         this.action = localValue7;
      }

      @Override
      public String type() {
         return "admin_invsee";
      }

      @Override
      public final String toString() {
         return "InternalType0085[nickname=" + this.nickname() + ", anarchy=" + this.anarchy() + ", server=" + this.server() + ", hash=" + this.hash() + ", slot=" + this.slot() + ", button=" + this.button() + ", action=" + this.action() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.nickname());
         result = 31 * result + java.util.Objects.hashCode(this.anarchy());
         result = 31 * result + java.util.Objects.hashCode(this.server());
         result = 31 * result + java.util.Objects.hashCode(this.hash());
         result = 31 * result + java.util.Objects.hashCode(this.slot());
         result = 31 * result + java.util.Objects.hashCode(this.button());
         result = 31 * result + java.util.Objects.hashCode(this.action());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0085 other = (Packets.InternalType0085) localValue1;
         return java.util.Objects.equals(this.nickname(), other.nickname())
            && java.util.Objects.equals(this.anarchy(), other.anarchy())
            && java.util.Objects.equals(this.server(), other.server())
            && java.util.Objects.equals(this.hash(), other.hash())
            && java.util.Objects.equals(this.slot(), other.slot())
            && java.util.Objects.equals(this.button(), other.button())
            && java.util.Objects.equals(this.action(), other.action());
      }

      public String nickname() {
         return this.nickname;
      }

      public String anarchy() {
         return this.anarchy;
      }

      public String server() {
         return this.server;
      }

      public String hash() {
         return this.hash;
      }

      public int slot() {
         return this.slot;
      }

      public int button() {
         return this.button;
      }

      public String action() {
         return this.action;
      }
   }

   public static final class InternalType0092 implements Packet {
      private final String username;

      public InternalType0092(String localValue1) {
         this.username = localValue1;
      }

      @Override
      public String type() {
         return "friend_removed";
      }

      @Override
      public final String toString() {
         return "InternalType0092[username=" + this.username() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.username());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0092 other = (Packets.InternalType0092) localValue1;
         return java.util.Objects.equals(this.username(), other.username());
      }

      public String username() {
         return this.username;
      }
   }

   public static final class InternalType0114 implements Packet {
      private final String username;
      private final String globalName;
      private final String avatarUrl;

      public InternalType0114(String localValue1, String localValue2, String localValue3) {
         this.username = localValue1;
         this.globalName = localValue2;
         this.avatarUrl = localValue3;
      }

      @Override
      public String type() {
         return "discord_info";
      }

      @Override
      public final String toString() {
         return "InternalType0114[username=" + this.username() + ", globalName=" + this.globalName() + ", avatarUrl=" + this.avatarUrl() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.username());
         result = 31 * result + java.util.Objects.hashCode(this.globalName());
         result = 31 * result + java.util.Objects.hashCode(this.avatarUrl());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0114 other = (Packets.InternalType0114) localValue1;
         return java.util.Objects.equals(this.username(), other.username())
            && java.util.Objects.equals(this.globalName(), other.globalName())
            && java.util.Objects.equals(this.avatarUrl(), other.avatarUrl());
      }

      public String username() {
         return this.username;
      }

      public String globalName() {
         return this.globalName;
      }

      public String avatarUrl() {
         return this.avatarUrl;
      }
   }

   public static final class InternalType0115 implements Packet {
      private final Packets.InternalType0018 author;
      private final String name;
      private final Packets.InternalType0068 vector;
      private final String anarchy;
      private final String server;
      private final String hash;
      private final long livingTime;
      private final int color;

      public InternalType0115(
         Packets.InternalType0018 localValue1, String localValue2, Packets.InternalType0068 localValue3, String localValue4, String localValue5, String localValue6, long localValue7, int localValue9
      ) {
         this.author = localValue1;
         this.name = localValue2;
         this.vector = localValue3;
         this.anarchy = localValue4;
         this.server = localValue5;
         this.hash = localValue6;
         this.livingTime = localValue7;
         this.color = localValue9;
      }

      @Override
      public String type() {
         return "create_way";
      }

      @Override
      public final String toString() {
         return "InternalType0115[author=" + this.author() + ", name=" + this.name() + ", vector=" + this.vector() + ", anarchy=" + this.anarchy() + ", server=" + this.server() + ", hash=" + this.hash() + ", livingTime=" + this.livingTime() + ", color=" + this.color() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.author());
         result = 31 * result + java.util.Objects.hashCode(this.name());
         result = 31 * result + java.util.Objects.hashCode(this.vector());
         result = 31 * result + java.util.Objects.hashCode(this.anarchy());
         result = 31 * result + java.util.Objects.hashCode(this.server());
         result = 31 * result + java.util.Objects.hashCode(this.hash());
         result = 31 * result + java.util.Objects.hashCode(this.livingTime());
         result = 31 * result + java.util.Objects.hashCode(this.color());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0115 other = (Packets.InternalType0115) localValue1;
         return java.util.Objects.equals(this.author(), other.author())
            && java.util.Objects.equals(this.name(), other.name())
            && java.util.Objects.equals(this.vector(), other.vector())
            && java.util.Objects.equals(this.anarchy(), other.anarchy())
            && java.util.Objects.equals(this.server(), other.server())
            && java.util.Objects.equals(this.hash(), other.hash())
            && java.util.Objects.equals(this.livingTime(), other.livingTime())
            && java.util.Objects.equals(this.color(), other.color());
      }

      public Packets.InternalType0018 author() {
         return this.author;
      }

      public String name() {
         return this.name;
      }

      public Packets.InternalType0068 vector() {
         return this.vector;
      }

      public String anarchy() {
         return this.anarchy;
      }

      public String server() {
         return this.server;
      }

      public String hash() {
         return this.hash;
      }

      public long livingTime() {
         return this.livingTime;
      }

      public int color() {
         return this.color;
      }
   }

   public static final class InternalType0116 implements Packet {
      private final String requester;
      private final int slot;
      private final int button;
      private final String action;

      public InternalType0116(String localValue1, int localValue2, int localValue3, String localValue4) {
         this.requester = localValue1;
         this.slot = localValue2;
         this.button = localValue3;
         this.action = localValue4;
      }

      @Override
      public String type() {
         return "admin_invsee_request";
      }

      @Override
      public final String toString() {
         return "InternalType0116[requester=" + this.requester() + ", slot=" + this.slot() + ", button=" + this.button() + ", action=" + this.action() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.requester());
         result = 31 * result + java.util.Objects.hashCode(this.slot());
         result = 31 * result + java.util.Objects.hashCode(this.button());
         result = 31 * result + java.util.Objects.hashCode(this.action());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0116 other = (Packets.InternalType0116) localValue1;
         return java.util.Objects.equals(this.requester(), other.requester())
            && java.util.Objects.equals(this.slot(), other.slot())
            && java.util.Objects.equals(this.button(), other.button())
            && java.util.Objects.equals(this.action(), other.action());
      }

      public String requester() {
         return this.requester;
      }

      public int slot() {
         return this.slot;
      }

      public int button() {
         return this.button;
      }

      public String action() {
         return this.action;
      }
   }

   public static final class InternalType0122 implements Packet {
      private final String action;
      private final String nickname;
      private final int count;

      public InternalType0122(String localValue1, String localValue2, int localValue3) {
         this.action = localValue1;
         this.nickname = localValue2;
         this.count = localValue3;
      }

      @Override
      public String type() {
         return "admin_result";
      }

      @Override
      public final String toString() {
         return "InternalType0122[action=" + this.action() + ", nickname=" + this.nickname() + ", count=" + this.count() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.action());
         result = 31 * result + java.util.Objects.hashCode(this.nickname());
         result = 31 * result + java.util.Objects.hashCode(this.count());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0122 other = (Packets.InternalType0122) localValue1;
         return java.util.Objects.equals(this.action(), other.action())
            && java.util.Objects.equals(this.nickname(), other.nickname())
            && java.util.Objects.equals(this.count(), other.count());
      }

      public String action() {
         return this.action;
      }

      public String nickname() {
         return this.nickname;
      }

      public int count() {
         return this.count;
      }
   }

   public static final class InternalType0126 implements Packet {
      private final String nickname;
      private final String anarchy;
      private final String server;
      private final String hash;

      public InternalType0126(String localValue1, String localValue2, String localValue3, String localValue4) {
         this.nickname = localValue1;
         this.anarchy = localValue2;
         this.server = localValue3;
         this.hash = localValue4;
      }

      @Override
      public String type() {
         return "admin_off";
      }

      @Override
      public final String toString() {
         return "InternalType0126[nickname=" + this.nickname() + ", anarchy=" + this.anarchy() + ", server=" + this.server() + ", hash=" + this.hash() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.nickname());
         result = 31 * result + java.util.Objects.hashCode(this.anarchy());
         result = 31 * result + java.util.Objects.hashCode(this.server());
         result = 31 * result + java.util.Objects.hashCode(this.hash());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0126 other = (Packets.InternalType0126) localValue1;
         return java.util.Objects.equals(this.nickname(), other.nickname())
            && java.util.Objects.equals(this.anarchy(), other.anarchy())
            && java.util.Objects.equals(this.server(), other.server())
            && java.util.Objects.equals(this.hash(), other.hash());
      }

      public String nickname() {
         return this.nickname;
      }

      public String anarchy() {
         return this.anarchy;
      }

      public String server() {
         return this.server;
      }

      public String hash() {
         return this.hash;
      }
   }

   public static final class InternalType0127 implements Packet {
      private final long shareId;

      public InternalType0127(long localValue1) {
         this.shareId = localValue1;
      }

      @Override
      public String type() {
         return "share_claim";
      }

      @Override
      public final String toString() {
         return "InternalType0127[shareId=" + this.shareId() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.shareId());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0127 other = (Packets.InternalType0127) localValue1;
         return java.util.Objects.equals(this.shareId(), other.shareId());
      }

      public long shareId() {
         return this.shareId;
      }
   }

   public static final class InternalType0133 implements Packet {
      private final String with;
      private final List<Packets.InternalType0144> messages;
      private final long beforeId;
      private final boolean more;

      public InternalType0133(String localValue1, List<Packets.InternalType0144> localValue2, long localValue3, boolean localValue5) {
         this.with = localValue1;
         this.messages = localValue2;
         this.beforeId = localValue3;
         this.more = localValue5;
      }

      @Override
      public String type() {
         return "private_history";
      }

      @Override
      public final String toString() {
         return "InternalType0133[with=" + this.with() + ", messages=" + this.messages() + ", beforeId=" + this.beforeId() + ", more=" + this.more() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.with());
         result = 31 * result + java.util.Objects.hashCode(this.messages());
         result = 31 * result + java.util.Objects.hashCode(this.beforeId());
         result = 31 * result + java.util.Objects.hashCode(this.more());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0133 other = (Packets.InternalType0133) localValue1;
         return java.util.Objects.equals(this.with(), other.with())
            && java.util.Objects.equals(this.messages(), other.messages())
            && java.util.Objects.equals(this.beforeId(), other.beforeId())
            && java.util.Objects.equals(this.more(), other.more());
      }

      public String with() {
         return this.with;
      }

      public List<Packets.InternalType0144> messages() {
         return this.messages;
      }

      public long beforeId() {
         return this.beforeId;
      }

      public boolean more() {
         return this.more;
      }
   }

   public static final class InternalType0134 implements Packet {
      private final String name;
      private final Packets.InternalType0068 vector;
      private final String anarchy;
      private final String server;
      private final String hash;

      public InternalType0134(String localValue1, Packets.InternalType0068 localValue2, String localValue3, String localValue4, String localValue5) {
         this.name = localValue1;
         this.vector = localValue2;
         this.anarchy = localValue3;
         this.server = localValue4;
         this.hash = localValue5;
      }

      @Override
      public String type() {
         return "update_way_send";
      }

      @Override
      public final String toString() {
         return "InternalType0134[name=" + this.name() + ", vector=" + this.vector() + ", anarchy=" + this.anarchy() + ", server=" + this.server() + ", hash=" + this.hash() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.name());
         result = 31 * result + java.util.Objects.hashCode(this.vector());
         result = 31 * result + java.util.Objects.hashCode(this.anarchy());
         result = 31 * result + java.util.Objects.hashCode(this.server());
         result = 31 * result + java.util.Objects.hashCode(this.hash());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0134 other = (Packets.InternalType0134) localValue1;
         return java.util.Objects.equals(this.name(), other.name())
            && java.util.Objects.equals(this.vector(), other.vector())
            && java.util.Objects.equals(this.anarchy(), other.anarchy())
            && java.util.Objects.equals(this.server(), other.server())
            && java.util.Objects.equals(this.hash(), other.hash());
      }

      public String name() {
         return this.name;
      }

      public Packets.InternalType0068 vector() {
         return this.vector;
      }

      public String anarchy() {
         return this.anarchy;
      }

      public String server() {
         return this.server;
      }

      public String hash() {
         return this.hash;
      }
   }

   public static final class InternalType0135 implements Packet {
      private final String message;

      public InternalType0135(String localValue1) {
         this.message = localValue1;
      }

      @Override
      public String type() {
         return "admin_broadcast";
      }

      @Override
      public final String toString() {
         return "InternalType0135[message=" + this.message() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.message());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0135 other = (Packets.InternalType0135) localValue1;
         return java.util.Objects.equals(this.message(), other.message());
      }

      public String message() {
         return this.message;
      }
   }

   public static final class InternalType0136 implements Packet {
      private final long id;
      private final Packets.InternalType0451 author;
      private final String toUsername;
      private final String message;

      public InternalType0136(long localValue1, Packets.InternalType0451 localValue3, String localValue4, String localValue5) {
         this.id = localValue1;
         this.author = localValue3;
         this.toUsername = localValue4;
         this.message = localValue5;
      }

      @Override
      public String type() {
         return "private_message";
      }

      @Override
      public final String toString() {
         return "InternalType0136[id=" + this.id() + ", author=" + this.author() + ", toUsername=" + this.toUsername() + ", message=" + this.message() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.id());
         result = 31 * result + java.util.Objects.hashCode(this.author());
         result = 31 * result + java.util.Objects.hashCode(this.toUsername());
         result = 31 * result + java.util.Objects.hashCode(this.message());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0136 other = (Packets.InternalType0136) localValue1;
         return java.util.Objects.equals(this.id(), other.id())
            && java.util.Objects.equals(this.author(), other.author())
            && java.util.Objects.equals(this.toUsername(), other.toUsername())
            && java.util.Objects.equals(this.message(), other.message());
      }

      public long id() {
         return this.id;
      }

      public Packets.InternalType0451 author() {
         return this.author;
      }

      public String toUsername() {
         return this.toUsername;
      }

      public String message() {
         return this.message;
      }
   }

   public static final class InternalType0144 {
      private final long id;
      private final Packets.InternalType0451 author;
      private final String toUsername;
      private final String message;
      private final long timestamp;

      public InternalType0144(long localValue1, Packets.InternalType0451 localValue3, String localValue4, String localValue5, long localValue6) {
         this.id = localValue1;
         this.author = localValue3;
         this.toUsername = localValue4;
         this.message = localValue5;
         this.timestamp = localValue6;
      }

      @Override
      public final String toString() {
         return "InternalType0144[id=" + this.id() + ", author=" + this.author() + ", toUsername=" + this.toUsername() + ", message=" + this.message() + ", timestamp=" + this.timestamp() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.id());
         result = 31 * result + java.util.Objects.hashCode(this.author());
         result = 31 * result + java.util.Objects.hashCode(this.toUsername());
         result = 31 * result + java.util.Objects.hashCode(this.message());
         result = 31 * result + java.util.Objects.hashCode(this.timestamp());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0144 other = (Packets.InternalType0144) localValue1;
         return java.util.Objects.equals(this.id(), other.id())
            && java.util.Objects.equals(this.author(), other.author())
            && java.util.Objects.equals(this.toUsername(), other.toUsername())
            && java.util.Objects.equals(this.message(), other.message())
            && java.util.Objects.equals(this.timestamp(), other.timestamp());
      }

      public long id() {
         return this.id;
      }

      public Packets.InternalType0451 author() {
         return this.author;
      }

      public String toUsername() {
         return this.toUsername;
      }

      public String message() {
         return this.message;
      }

      public long timestamp() {
         return this.timestamp;
      }
   }

   public static final class InternalType0145 implements Packet {
      private final String name;
      private final String language;
      private final String source;
      private final List<String> libraries;

      public InternalType0145(String localValue1, String localValue2, String localValue3, List<String> localValue4) {
         this.name = localValue1;
         this.language = localValue2;
         this.source = localValue3;
         this.libraries = localValue4;
      }

      @Override
      public String type() {
         return "script_apply";
      }

      @Override
      public final String toString() {
         return "InternalType0145[name=" + this.name() + ", language=" + this.language() + ", source=" + this.source() + ", libraries=" + this.libraries() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.name());
         result = 31 * result + java.util.Objects.hashCode(this.language());
         result = 31 * result + java.util.Objects.hashCode(this.source());
         result = 31 * result + java.util.Objects.hashCode(this.libraries());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0145 other = (Packets.InternalType0145) localValue1;
         return java.util.Objects.equals(this.name(), other.name())
            && java.util.Objects.equals(this.language(), other.language())
            && java.util.Objects.equals(this.source(), other.source())
            && java.util.Objects.equals(this.libraries(), other.libraries());
      }

      public String name() {
         return this.name;
      }

      public String language() {
         return this.language;
      }

      public String source() {
         return this.source;
      }

      public List<String> libraries() {
         return this.libraries;
      }
   }

   public static final class InternalType0150 implements Packet {
      private final String username;
      private final String email;
      private final String password;
      private final String client;

      public InternalType0150(String localValue1, String localValue2, String localValue3, String localValue4) {
         this.username = localValue1;
         this.email = localValue2;
         this.password = localValue3;
         this.client = localValue4;
      }

      @Override
      public String type() {
         return "register";
      }

      @Override
      public final String toString() {
         return "InternalType0150[username=" + this.username() + ", email=" + this.email() + ", password=" + this.password() + ", client=" + this.client() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.username());
         result = 31 * result + java.util.Objects.hashCode(this.email());
         result = 31 * result + java.util.Objects.hashCode(this.password());
         result = 31 * result + java.util.Objects.hashCode(this.client());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0150 other = (Packets.InternalType0150) localValue1;
         return java.util.Objects.equals(this.username(), other.username())
            && java.util.Objects.equals(this.email(), other.email())
            && java.util.Objects.equals(this.password(), other.password())
            && java.util.Objects.equals(this.client(), other.client());
      }

      public String username() {
         return this.username;
      }

      public String email() {
         return this.email;
      }

      public String password() {
         return this.password;
      }

      public String client() {
         return this.client;
      }
   }

   public static final class InternalType0151 implements Packet {
      private final String clientName;
      private final String version;

      public InternalType0151(String localValue1, String localValue2) {
         this.clientName = localValue1;
         this.version = localValue2;
      }

      @Override
      public String type() {
         return "client_start";
      }

      @Override
      public final String toString() {
         return "InternalType0151[clientName=" + this.clientName() + ", version=" + this.version() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.clientName());
         result = 31 * result + java.util.Objects.hashCode(this.version());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0151 other = (Packets.InternalType0151) localValue1;
         return java.util.Objects.equals(this.clientName(), other.clientName())
            && java.util.Objects.equals(this.version(), other.version());
      }

      public String clientName() {
         return this.clientName;
      }

      public String version() {
         return this.version;
      }
   }

   public static final class InternalType0154 implements Packet {
      private final String query;
      private final int page;

      public InternalType0154(String localValue1, int localValue2) {
         this.query = localValue1;
         this.page = localValue2;
      }

      @Override
      public String type() {
         return "people_request";
      }

      @Override
      public final String toString() {
         return "InternalType0154[query=" + this.query() + ", page=" + this.page() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.query());
         result = 31 * result + java.util.Objects.hashCode(this.page());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0154 other = (Packets.InternalType0154) localValue1;
         return java.util.Objects.equals(this.query(), other.query())
            && java.util.Objects.equals(this.page(), other.page());
      }

      public String query() {
         return this.query;
      }

      public int page() {
         return this.page;
      }
   }

   public static final class InternalType0166 implements Packet {
      private final String fromUsername;

      public InternalType0166(String localValue1) {
         this.fromUsername = localValue1;
      }

      @Override
      public String type() {
         return "friend_request_received";
      }

      @Override
      public final String toString() {
         return "InternalType0166[fromUsername=" + this.fromUsername() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.fromUsername());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0166 other = (Packets.InternalType0166) localValue1;
         return java.util.Objects.equals(this.fromUsername(), other.fromUsername());
      }

      public String fromUsername() {
         return this.fromUsername;
      }
   }

   public static final class InternalType0177 implements Packet {
      private final String shareKind;
      private final String name;
      private final boolean ok;
      private final String error;

      public InternalType0177(String localValue1, String localValue2, boolean localValue3, String localValue4) {
         this.shareKind = localValue1;
         this.name = localValue2;
         this.ok = localValue3;
         this.error = localValue4;
      }

      @Override
      public String type() {
         return "share_result";
      }

      @Override
      public final String toString() {
         return "InternalType0177[shareKind=" + this.shareKind() + ", name=" + this.name() + ", ok=" + this.ok() + ", error=" + this.error() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.shareKind());
         result = 31 * result + java.util.Objects.hashCode(this.name());
         result = 31 * result + java.util.Objects.hashCode(this.ok());
         result = 31 * result + java.util.Objects.hashCode(this.error());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0177 other = (Packets.InternalType0177) localValue1;
         return java.util.Objects.equals(this.shareKind(), other.shareKind())
            && java.util.Objects.equals(this.name(), other.name())
            && java.util.Objects.equals(this.ok(), other.ok())
            && java.util.Objects.equals(this.error(), other.error());
      }

      public String shareKind() {
         return this.shareKind;
      }

      public String name() {
         return this.name;
      }

      public boolean ok() {
         return this.ok;
      }

      public String error() {
         return this.error;
      }
   }

   public static final class InternalType0182 implements Packet {
      private final long beforeId;

      public InternalType0182(long localValue1) {
         this.beforeId = localValue1;
      }

      @Override
      public String type() {
         return "chat_history_request";
      }

      @Override
      public final String toString() {
         return "InternalType0182[beforeId=" + this.beforeId() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.beforeId());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0182 other = (Packets.InternalType0182) localValue1;
         return java.util.Objects.equals(this.beforeId(), other.beforeId());
      }

      public long beforeId() {
         return this.beforeId;
      }
   }

   public static final class InternalType0183 implements Packet {
      private final List<String> requests;

      public InternalType0183(List<String> localValue1) {
         this.requests = localValue1;
      }

      @Override
      public String type() {
         return "friend_requests_update";
      }

      @Override
      public final String toString() {
         return "InternalType0183[requests=" + this.requests() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.requests());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0183 other = (Packets.InternalType0183) localValue1;
         return java.util.Objects.equals(this.requests(), other.requests());
      }

      public List<String> requests() {
         return this.requests;
      }
   }

   public static final class InternalType0184 implements Packet {
      private final List<Packets.InternalType0018> players;

      public InternalType0184(List<Packets.InternalType0018> localValue1) {
         this.players = localValue1;
      }

      @Override
      public String type() {
         return "visible_players_update";
      }

      @Override
      public final String toString() {
         return "InternalType0184[players=" + this.players() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.players());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0184 other = (Packets.InternalType0184) localValue1;
         return java.util.Objects.equals(this.players(), other.players());
      }

      public List<Packets.InternalType0018> players() {
         return this.players;
      }
   }

   public static final class InternalType0185 implements Packet {
      private final String client;
      private final String username;
      private final String role;
      private final String activity;

      public InternalType0185(String localValue1, String localValue2, String localValue3, String localValue4) {
         this.client = localValue1;
         this.username = localValue2;
         this.role = localValue3;
         this.activity = localValue4;
      }

      @Override
      public String type() {
         return "user_auth";
      }

      @Override
      public final String toString() {
         return "InternalType0185[client=" + this.client() + ", username=" + this.username() + ", role=" + this.role() + ", activity=" + this.activity() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.client());
         result = 31 * result + java.util.Objects.hashCode(this.username());
         result = 31 * result + java.util.Objects.hashCode(this.role());
         result = 31 * result + java.util.Objects.hashCode(this.activity());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0185 other = (Packets.InternalType0185) localValue1;
         return java.util.Objects.equals(this.client(), other.client())
            && java.util.Objects.equals(this.username(), other.username())
            && java.util.Objects.equals(this.role(), other.role())
            && java.util.Objects.equals(this.activity(), other.activity());
      }

      public String client() {
         return this.client;
      }

      public String username() {
         return this.username;
      }

      public String role() {
         return this.role;
      }

      public String activity() {
         return this.activity;
      }
   }

   public static final class InternalType0196 implements Packet {
      private final String nickname;
      private final String anarchy;
      private final String server;
      private final String hash;

      public InternalType0196(String localValue1, String localValue2, String localValue3, String localValue4) {
         this.nickname = localValue1;
         this.anarchy = localValue2;
         this.server = localValue3;
         this.hash = localValue4;
      }

      @Override
      public String type() {
         return "admin_unfreeze";
      }

      @Override
      public final String toString() {
         return "InternalType0196[nickname=" + this.nickname() + ", anarchy=" + this.anarchy() + ", server=" + this.server() + ", hash=" + this.hash() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.nickname());
         result = 31 * result + java.util.Objects.hashCode(this.anarchy());
         result = 31 * result + java.util.Objects.hashCode(this.server());
         result = 31 * result + java.util.Objects.hashCode(this.hash());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0196 other = (Packets.InternalType0196) localValue1;
         return java.util.Objects.equals(this.nickname(), other.nickname())
            && java.util.Objects.equals(this.anarchy(), other.anarchy())
            && java.util.Objects.equals(this.server(), other.server())
            && java.util.Objects.equals(this.hash(), other.hash());
      }

      public String nickname() {
         return this.nickname;
      }

      public String anarchy() {
         return this.anarchy;
      }

      public String server() {
         return this.server;
      }

      public String hash() {
         return this.hash;
      }
   }

   public static final class InternalType0220 implements Packet {
      private final long until;
      private final String reason;

      public InternalType0220(long localValue1, String localValue3) {
         this.until = localValue1;
         this.reason = localValue3;
      }

      @Override
      public String type() {
         return "chat_muted";
      }

      @Override
      public final String toString() {
         return "InternalType0220[until=" + this.until() + ", reason=" + this.reason() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.until());
         result = 31 * result + java.util.Objects.hashCode(this.reason());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0220 other = (Packets.InternalType0220) localValue1;
         return java.util.Objects.equals(this.until(), other.until())
            && java.util.Objects.equals(this.reason(), other.reason());
      }

      public long until() {
         return this.until;
      }

      public String reason() {
         return this.reason;
      }
   }

   public static final class InternalType0234 implements Packet {
      private final int total;
      private final int guests;
      private final int site;

      public InternalType0234(int localValue1, int localValue2, int localValue3) {
         this.total = localValue1;
         this.guests = localValue2;
         this.site = localValue3;
      }

      @Override
      public String type() {
         return "online_count";
      }

      @Override
      public final String toString() {
         return "InternalType0234[total=" + this.total() + ", guests=" + this.guests() + ", site=" + this.site() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.total());
         result = 31 * result + java.util.Objects.hashCode(this.guests());
         result = 31 * result + java.util.Objects.hashCode(this.site());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0234 other = (Packets.InternalType0234) localValue1;
         return java.util.Objects.equals(this.total(), other.total())
            && java.util.Objects.equals(this.guests(), other.guests())
            && java.util.Objects.equals(this.site(), other.site());
      }

      public int total() {
         return this.total;
      }

      public int guests() {
         return this.guests;
      }

      public int site() {
         return this.site;
      }
   }

   public static final class InternalType0235 implements Packet {
      private final String username;
      private final String password;
      private final String client;

      public InternalType0235(String localValue1, String localValue2, String localValue3) {
         this.username = localValue1;
         this.password = localValue2;
         this.client = localValue3;
      }

      @Override
      public String type() {
         return "login";
      }

      @Override
      public final String toString() {
         return "InternalType0235[username=" + this.username() + ", password=" + this.password() + ", client=" + this.client() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.username());
         result = 31 * result + java.util.Objects.hashCode(this.password());
         result = 31 * result + java.util.Objects.hashCode(this.client());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0235 other = (Packets.InternalType0235) localValue1;
         return java.util.Objects.equals(this.username(), other.username())
            && java.util.Objects.equals(this.password(), other.password())
            && java.util.Objects.equals(this.client(), other.client());
      }

      public String username() {
         return this.username;
      }

      public String password() {
         return this.password;
      }

      public String client() {
         return this.client;
      }
   }

   public static final class InternalType0236 implements Packet {
      private final String username;

      public InternalType0236(String localValue1) {
         this.username = localValue1;
      }

      @Override
      public String type() {
         return "friend_remove";
      }

      @Override
      public final String toString() {
         return "InternalType0236[username=" + this.username() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.username());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0236 other = (Packets.InternalType0236) localValue1;
         return java.util.Objects.equals(this.username(), other.username());
      }

      public String username() {
         return this.username;
      }
   }

   public static final class InternalType0237 implements Packet {
      private final String action;
      private final String username;
      private final boolean ok;

      public InternalType0237(String localValue1, String localValue2, boolean localValue3) {
         this.action = localValue1;
         this.username = localValue2;
         this.ok = localValue3;
      }

      @Override
      public String type() {
         return "mod_result";
      }

      @Override
      public final String toString() {
         return "InternalType0237[action=" + this.action() + ", username=" + this.username() + ", ok=" + this.ok() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.action());
         result = 31 * result + java.util.Objects.hashCode(this.username());
         result = 31 * result + java.util.Objects.hashCode(this.ok());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0237 other = (Packets.InternalType0237) localValue1;
         return java.util.Objects.equals(this.action(), other.action())
            && java.util.Objects.equals(this.username(), other.username())
            && java.util.Objects.equals(this.ok(), other.ok());
      }

      public String action() {
         return this.action;
      }

      public String username() {
         return this.username;
      }

      public boolean ok() {
         return this.ok;
      }
   }

   public static final class InternalType0238 {
      private final String name;
      private final String language;
      private final String source;
      private final List<String> libraries;

      public InternalType0238(String localValue1, String localValue2, String localValue3, List<String> localValue4) {
         this.name = localValue1;
         this.language = localValue2;
         this.source = localValue3;
         this.libraries = localValue4;
      }

      @Override
      public final String toString() {
         return "InternalType0238[name=" + this.name() + ", language=" + this.language() + ", source=" + this.source() + ", libraries=" + this.libraries() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.name());
         result = 31 * result + java.util.Objects.hashCode(this.language());
         result = 31 * result + java.util.Objects.hashCode(this.source());
         result = 31 * result + java.util.Objects.hashCode(this.libraries());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0238 other = (Packets.InternalType0238) localValue1;
         return java.util.Objects.equals(this.name(), other.name())
            && java.util.Objects.equals(this.language(), other.language())
            && java.util.Objects.equals(this.source(), other.source())
            && java.util.Objects.equals(this.libraries(), other.libraries());
      }

      public String name() {
         return this.name;
      }

      public String language() {
         return this.language;
      }

      public String source() {
         return this.source;
      }

      public List<String> libraries() {
         return this.libraries;
      }
   }

   public static final class InternalType0239 implements Packet {
      @Override
      public String type() {
         return "script_pull";
      }

      @Override
      public final String toString() {
         return "InternalType0239[]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0239 other = (Packets.InternalType0239) localValue1;
         return true;
      }
   }

   public static final class InternalType0240 implements Packet {
      private final String name;
      private final String language;
      private final String source;

      public InternalType0240(String localValue1, String localValue2, String localValue3) {
         this.name = localValue1;
         this.language = localValue2;
         this.source = localValue3;
      }

      @Override
      public String type() {
         return "script_save";
      }

      @Override
      public final String toString() {
         return "InternalType0240[name=" + this.name() + ", language=" + this.language() + ", source=" + this.source() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.name());
         result = 31 * result + java.util.Objects.hashCode(this.language());
         result = 31 * result + java.util.Objects.hashCode(this.source());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0240 other = (Packets.InternalType0240) localValue1;
         return java.util.Objects.equals(this.name(), other.name())
            && java.util.Objects.equals(this.language(), other.language())
            && java.util.Objects.equals(this.source(), other.source());
      }

      public String name() {
         return this.name;
      }

      public String language() {
         return this.language;
      }

      public String source() {
         return this.source;
      }
   }

   public static final class InternalType0242 implements Packet {
      private final long id;

      public InternalType0242(long localValue1) {
         this.id = localValue1;
      }

      @Override
      public String type() {
         return "config_delete";
      }

      @Override
      public final String toString() {
         return "InternalType0242[id=" + this.id() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.id());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0242 other = (Packets.InternalType0242) localValue1;
         return java.util.Objects.equals(this.id(), other.id());
      }

      public long id() {
         return this.id;
      }
   }

   public static final class InternalType0243 implements Packet {
      private final List<Packets.InternalType0424> styles;

      public InternalType0243(List<Packets.InternalType0424> localValue1) {
         this.styles = localValue1;
      }

      @Override
      public String type() {
         return "cosmetics_catalog";
      }

      @Override
      public final String toString() {
         return "InternalType0243[styles=" + this.styles() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.styles());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0243 other = (Packets.InternalType0243) localValue1;
         return java.util.Objects.equals(this.styles(), other.styles());
      }

      public List<Packets.InternalType0424> styles() {
         return this.styles;
      }
   }

   public static final class InternalType0249 implements Packet {
      private final String username;

      public InternalType0249(String localValue1) {
         this.username = localValue1;
      }

      @Override
      public String type() {
         return "mod_unmute";
      }

      @Override
      public final String toString() {
         return "InternalType0249[username=" + this.username() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.username());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0249 other = (Packets.InternalType0249) localValue1;
         return java.util.Objects.equals(this.username(), other.username());
      }

      public String username() {
         return this.username;
      }
   }

   public static final class InternalType0269 implements Packet {
      private final String name;
      private final JsonObject data;

      public InternalType0269(String localValue1, JsonObject localValue2) {
         this.name = localValue1;
         this.data = localValue2;
      }

      @Override
      public String type() {
         return "config_import";
      }

      @Override
      public final String toString() {
         return "InternalType0269[name=" + this.name() + ", data=" + this.data() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.name());
         result = 31 * result + java.util.Objects.hashCode(this.data());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0269 other = (Packets.InternalType0269) localValue1;
         return java.util.Objects.equals(this.name(), other.name())
            && java.util.Objects.equals(this.data(), other.data());
      }

      public String name() {
         return this.name;
      }

      public JsonObject data() {
         return this.data;
      }
   }

   public static final class InternalType0271 implements Packet {
      private final String payload;

      public InternalType0271(String localValue1) {
         this.payload = localValue1;
      }

      @Override
      public String type() {
         return "key_exchange";
      }

      @Override
      public final String toString() {
         return "InternalType0271[payload=" + this.payload() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.payload());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0271 other = (Packets.InternalType0271) localValue1;
         return java.util.Objects.equals(this.payload(), other.payload());
      }

      public String payload() {
         return this.payload;
      }
   }

   public static final class InternalType0277 implements Packet {
      private final String username;

      public InternalType0277(String localValue1) {
         this.username = localValue1;
      }

      @Override
      public String type() {
         return "friend_request_accept";
      }

      @Override
      public final String toString() {
         return "InternalType0277[username=" + this.username() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.username());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0277 other = (Packets.InternalType0277) localValue1;
         return java.util.Objects.equals(this.username(), other.username());
      }

      public String username() {
         return this.username;
      }
   }

   public static final class InternalType0280 implements Packet {
      private final String toUsername;
      private final String error;

      public InternalType0280(String localValue1, String localValue2) {
         this.toUsername = localValue1;
         this.error = localValue2;
      }

      @Override
      public String type() {
         return "private_message_error";
      }

      @Override
      public final String toString() {
         return "InternalType0280[toUsername=" + this.toUsername() + ", error=" + this.error() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.toUsername());
         result = 31 * result + java.util.Objects.hashCode(this.error());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0280 other = (Packets.InternalType0280) localValue1;
         return java.util.Objects.equals(this.toUsername(), other.toUsername())
            && java.util.Objects.equals(this.error(), other.error());
      }

      public String toUsername() {
         return this.toUsername;
      }

      public String error() {
         return this.error;
      }
   }

   public static final class InternalType0283 implements Packet {
      private final JsonObject data;

      public InternalType0283(JsonObject localValue1) {
         this.data = localValue1;
      }

      @Override
      public String type() {
         return "client_data_save";
      }

      @Override
      public final String toString() {
         return "InternalType0283[data=" + this.data() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.data());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0283 other = (Packets.InternalType0283) localValue1;
         return java.util.Objects.equals(this.data(), other.data());
      }

      public JsonObject data() {
         return this.data;
      }
   }

   public static final class InternalType0284 implements Packet {
      @Override
      public String type() {
         return "client_data_sync";
      }

      @Override
      public final String toString() {
         return "InternalType0284[]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0284 other = (Packets.InternalType0284) localValue1;
         return true;
      }
   }

   public static final class InternalType0289 implements Packet {
      private final String activity;

      public InternalType0289(String localValue1) {
         this.activity = localValue1;
      }

      @Override
      public String type() {
         return "activity_update";
      }

      @Override
      public final String toString() {
         return "InternalType0289[activity=" + this.activity() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.activity());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0289 other = (Packets.InternalType0289) localValue1;
         return java.util.Objects.equals(this.activity(), other.activity());
      }

      public String activity() {
         return this.activity;
      }
   }

   public static final class InternalType0306 implements Packet {
      private final String username;
      private final int uid;
      private final String role;
      private final String badge;
      private final String nickStyle;
      private final boolean online;
      private final boolean inGame;
      private final String status;
      private final long lastSeen;
      private final long registered;
      private final long playtime;
      private final int friendCount;
      private final List<String> mutual;
      private final int mutualCount;
      private final String relationship;
      private final long mutedUntil;

      public InternalType0306(
         String localValue1,
         int localValue2,
         String localValue3,
         String localValue4,
         String localValue5,
         boolean localValue6,
         boolean localValue7,
         String localValue8,
         long localValue9,
         long localValue11,
         long localValue13,
         int localValue15,
         List<String> localValue16,
         int localValue17,
         String localValue18,
         long localValue19
      ) {
         this.username = localValue1;
         this.uid = localValue2;
         this.role = localValue3;
         this.badge = localValue4;
         this.nickStyle = localValue5;
         this.online = localValue6;
         this.inGame = localValue7;
         this.status = localValue8;
         this.lastSeen = localValue9;
         this.registered = localValue11;
         this.playtime = localValue13;
         this.friendCount = localValue15;
         this.mutual = localValue16;
         this.mutualCount = localValue17;
         this.relationship = localValue18;
         this.mutedUntil = localValue19;
      }

      @Override
      public String type() {
         return "profile";
      }

      @Override
      public final String toString() {
         return "InternalType0306[username=" + this.username() + ", uid=" + this.uid() + ", role=" + this.role() + ", badge=" + this.badge() + ", nickStyle=" + this.nickStyle() + ", online=" + this.online() + ", inGame=" + this.inGame() + ", status=" + this.status() + ", lastSeen=" + this.lastSeen() + ", registered=" + this.registered() + ", playtime=" + this.playtime() + ", friendCount=" + this.friendCount() + ", mutual=" + this.mutual() + ", mutualCount=" + this.mutualCount() + ", relationship=" + this.relationship() + ", mutedUntil=" + this.mutedUntil() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.username());
         result = 31 * result + java.util.Objects.hashCode(this.uid());
         result = 31 * result + java.util.Objects.hashCode(this.role());
         result = 31 * result + java.util.Objects.hashCode(this.badge());
         result = 31 * result + java.util.Objects.hashCode(this.nickStyle());
         result = 31 * result + java.util.Objects.hashCode(this.online());
         result = 31 * result + java.util.Objects.hashCode(this.inGame());
         result = 31 * result + java.util.Objects.hashCode(this.status());
         result = 31 * result + java.util.Objects.hashCode(this.lastSeen());
         result = 31 * result + java.util.Objects.hashCode(this.registered());
         result = 31 * result + java.util.Objects.hashCode(this.playtime());
         result = 31 * result + java.util.Objects.hashCode(this.friendCount());
         result = 31 * result + java.util.Objects.hashCode(this.mutual());
         result = 31 * result + java.util.Objects.hashCode(this.mutualCount());
         result = 31 * result + java.util.Objects.hashCode(this.relationship());
         result = 31 * result + java.util.Objects.hashCode(this.mutedUntil());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0306 other = (Packets.InternalType0306) localValue1;
         return java.util.Objects.equals(this.username(), other.username())
            && java.util.Objects.equals(this.uid(), other.uid())
            && java.util.Objects.equals(this.role(), other.role())
            && java.util.Objects.equals(this.badge(), other.badge())
            && java.util.Objects.equals(this.nickStyle(), other.nickStyle())
            && java.util.Objects.equals(this.online(), other.online())
            && java.util.Objects.equals(this.inGame(), other.inGame())
            && java.util.Objects.equals(this.status(), other.status())
            && java.util.Objects.equals(this.lastSeen(), other.lastSeen())
            && java.util.Objects.equals(this.registered(), other.registered())
            && java.util.Objects.equals(this.playtime(), other.playtime())
            && java.util.Objects.equals(this.friendCount(), other.friendCount())
            && java.util.Objects.equals(this.mutual(), other.mutual())
            && java.util.Objects.equals(this.mutualCount(), other.mutualCount())
            && java.util.Objects.equals(this.relationship(), other.relationship())
            && java.util.Objects.equals(this.mutedUntil(), other.mutedUntil());
      }

      public String username() {
         return this.username;
      }

      public int uid() {
         return this.uid;
      }

      public String role() {
         return this.role;
      }

      public String badge() {
         return this.badge;
      }

      public String nickStyle() {
         return this.nickStyle;
      }

      public boolean online() {
         return this.online;
      }

      public boolean inGame() {
         return this.inGame;
      }

      public String status() {
         return this.status;
      }

      public long lastSeen() {
         return this.lastSeen;
      }

      public long registered() {
         return this.registered;
      }

      public long playtime() {
         return this.playtime;
      }

      public int friendCount() {
         return this.friendCount;
      }

      public List<String> mutual() {
         return this.mutual;
      }

      public int mutualCount() {
         return this.mutualCount;
      }

      public String relationship() {
         return this.relationship;
      }

      public long mutedUntil() {
         return this.mutedUntil;
      }
   }

   public static final class InternalType0321 implements Packet {
      private final long id;
      private final String name;

      public InternalType0321(long localValue1, String localValue3) {
         this.id = localValue1;
         this.name = localValue3;
      }

      @Override
      public String type() {
         return "config_rename";
      }

      @Override
      public final String toString() {
         return "InternalType0321[id=" + this.id() + ", name=" + this.name() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.id());
         result = 31 * result + java.util.Objects.hashCode(this.name());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0321 other = (Packets.InternalType0321) localValue1;
         return java.util.Objects.equals(this.id(), other.id())
            && java.util.Objects.equals(this.name(), other.name());
      }

      public long id() {
         return this.id;
      }

      public String name() {
         return this.name;
      }
   }

   public static final class InternalType0338 implements Packet {
      private final Packets.InternalType0018 author;
      private final String name;
      private final Packets.InternalType0068 vector;
      private final String anarchy;
      private final String server;
      private final String hash;

      public InternalType0338(Packets.InternalType0018 localValue1, String localValue2, Packets.InternalType0068 localValue3, String localValue4, String localValue5, String localValue6) {
         this.author = localValue1;
         this.name = localValue2;
         this.vector = localValue3;
         this.anarchy = localValue4;
         this.server = localValue5;
         this.hash = localValue6;
      }

      @Override
      public String type() {
         return "update_way";
      }

      @Override
      public final String toString() {
         return "InternalType0338[author=" + this.author() + ", name=" + this.name() + ", vector=" + this.vector() + ", anarchy=" + this.anarchy() + ", server=" + this.server() + ", hash=" + this.hash() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.author());
         result = 31 * result + java.util.Objects.hashCode(this.name());
         result = 31 * result + java.util.Objects.hashCode(this.vector());
         result = 31 * result + java.util.Objects.hashCode(this.anarchy());
         result = 31 * result + java.util.Objects.hashCode(this.server());
         result = 31 * result + java.util.Objects.hashCode(this.hash());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0338 other = (Packets.InternalType0338) localValue1;
         return java.util.Objects.equals(this.author(), other.author())
            && java.util.Objects.equals(this.name(), other.name())
            && java.util.Objects.equals(this.vector(), other.vector())
            && java.util.Objects.equals(this.anarchy(), other.anarchy())
            && java.util.Objects.equals(this.server(), other.server())
            && java.util.Objects.equals(this.hash(), other.hash());
      }

      public Packets.InternalType0018 author() {
         return this.author;
      }

      public String name() {
         return this.name;
      }

      public Packets.InternalType0068 vector() {
         return this.vector;
      }

      public String anarchy() {
         return this.anarchy;
      }

      public String server() {
         return this.server;
      }

      public String hash() {
         return this.hash;
      }
   }

   public static final class InternalType0339 implements Packet {
      private final String from;
      private final String to;

      public InternalType0339(String localValue1, String localValue2) {
         this.from = localValue1;
         this.to = localValue2;
      }

      @Override
      public String type() {
         return "script_rename_apply";
      }

      @Override
      public final String toString() {
         return "InternalType0339[from=" + this.from() + ", to=" + this.to() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.from());
         result = 31 * result + java.util.Objects.hashCode(this.to());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0339 other = (Packets.InternalType0339) localValue1;
         return java.util.Objects.equals(this.from(), other.from())
            && java.util.Objects.equals(this.to(), other.to());
      }

      public String from() {
         return this.from;
      }

      public String to() {
         return this.to;
      }
   }

   public static final class InternalType0377 implements Packet {
      private final String username;

      public InternalType0377(String localValue1) {
         this.username = localValue1;
      }

      @Override
      public String type() {
         return "friend_request_reject";
      }

      @Override
      public final String toString() {
         return "InternalType0377[username=" + this.username() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.username());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0377 other = (Packets.InternalType0377) localValue1;
         return java.util.Objects.equals(this.username(), other.username());
      }

      public String username() {
         return this.username;
      }
   }

   public static final class InternalType0383 implements Packet {
      private final long id;

      public InternalType0383(long localValue1) {
         this.id = localValue1;
      }

      @Override
      public String type() {
         return "mod_delete";
      }

      @Override
      public final String toString() {
         return "InternalType0383[id=" + this.id() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.id());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0383 other = (Packets.InternalType0383) localValue1;
         return java.util.Objects.equals(this.id(), other.id());
      }

      public long id() {
         return this.id;
      }
   }

   public static final class InternalType0386 {
      private final double offset;
      private final int color;

      public InternalType0386(double localValue1, int localValue3) {
         this.offset = localValue1;
         this.color = localValue3;
      }

      @Override
      public final String toString() {
         return "InternalType0386[offset=" + this.offset() + ", color=" + this.color() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.offset());
         result = 31 * result + java.util.Objects.hashCode(this.color());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0386 other = (Packets.InternalType0386) localValue1;
         return java.util.Objects.equals(this.offset(), other.offset())
            && java.util.Objects.equals(this.color(), other.color());
      }

      public double offset() {
         return this.offset;
      }

      public int color() {
         return this.color;
      }
   }

   public static final class InternalType0388 implements Packet {
      private final String nickname;
      private final String anarchy;
      private final String server;
      private final String ip;
      private final String hash;
      private final String visibility;
      private final String clientName;

      public InternalType0388(String localValue1, String localValue2, String localValue3, String localValue4, String localValue5, String localValue6, String localValue7) {
         this.nickname = localValue1;
         this.anarchy = localValue2;
         this.server = localValue3;
         this.ip = localValue4;
         this.hash = localValue5;
         this.visibility = localValue6;
         this.clientName = localValue7;
      }

      @Override
      public String type() {
         return "game_info_update";
      }

      @Override
      public final String toString() {
         return "InternalType0388[nickname=" + this.nickname() + ", anarchy=" + this.anarchy() + ", server=" + this.server() + ", ip=" + this.ip() + ", hash=" + this.hash() + ", visibility=" + this.visibility() + ", clientName=" + this.clientName() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.nickname());
         result = 31 * result + java.util.Objects.hashCode(this.anarchy());
         result = 31 * result + java.util.Objects.hashCode(this.server());
         result = 31 * result + java.util.Objects.hashCode(this.ip());
         result = 31 * result + java.util.Objects.hashCode(this.hash());
         result = 31 * result + java.util.Objects.hashCode(this.visibility());
         result = 31 * result + java.util.Objects.hashCode(this.clientName());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0388 other = (Packets.InternalType0388) localValue1;
         return java.util.Objects.equals(this.nickname(), other.nickname())
            && java.util.Objects.equals(this.anarchy(), other.anarchy())
            && java.util.Objects.equals(this.server(), other.server())
            && java.util.Objects.equals(this.ip(), other.ip())
            && java.util.Objects.equals(this.hash(), other.hash())
            && java.util.Objects.equals(this.visibility(), other.visibility())
            && java.util.Objects.equals(this.clientName(), other.clientName());
      }

      public String nickname() {
         return this.nickname;
      }

      public String anarchy() {
         return this.anarchy;
      }

      public String server() {
         return this.server;
      }

      public String ip() {
         return this.ip;
      }

      public String hash() {
         return this.hash;
      }

      public String visibility() {
         return this.visibility;
      }

      public String clientName() {
         return this.clientName;
      }
   }

   public static final class InternalType0389 implements Packet {
      private final long id;
      private final String toUsername;

      public InternalType0389(long localValue1, String localValue3) {
         this.id = localValue1;
         this.toUsername = localValue3;
      }

      @Override
      public String type() {
         return "message_deleted";
      }

      @Override
      public final String toString() {
         return "InternalType0389[id=" + this.id() + ", toUsername=" + this.toUsername() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.id());
         result = 31 * result + java.util.Objects.hashCode(this.toUsername());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0389 other = (Packets.InternalType0389) localValue1;
         return java.util.Objects.equals(this.id(), other.id())
            && java.util.Objects.equals(this.toUsername(), other.toUsername());
      }

      public long id() {
         return this.id;
      }

      public String toUsername() {
         return this.toUsername;
      }
   }

   public static final class InternalType0390 implements Packet {
      private final String username;

      public InternalType0390(String localValue1) {
         this.username = localValue1;
      }

      @Override
      public String type() {
         return "friend_added";
      }

      @Override
      public final String toString() {
         return "InternalType0390[username=" + this.username() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.username());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0390 other = (Packets.InternalType0390) localValue1;
         return java.util.Objects.equals(this.username(), other.username());
      }

      public String username() {
         return this.username;
      }
   }

   public static final class InternalType0391 implements Packet {
      private final String reqId;
      private final String action;
      private final String key;
      private final String option;
      private final JsonElement value;

      public InternalType0391(String localValue1, String localValue2, String localValue3, String localValue4, JsonElement localValue5) {
         this.reqId = localValue1;
         this.action = localValue2;
         this.key = localValue3;
         this.option = localValue4;
         this.value = localValue5;
      }

      @Override
      public String type() {
         return "autofarm_request";
      }

      @Override
      public final String toString() {
         return "InternalType0391[reqId=" + this.reqId() + ", action=" + this.action() + ", key=" + this.key() + ", option=" + this.option() + ", value=" + this.value() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.reqId());
         result = 31 * result + java.util.Objects.hashCode(this.action());
         result = 31 * result + java.util.Objects.hashCode(this.key());
         result = 31 * result + java.util.Objects.hashCode(this.option());
         result = 31 * result + java.util.Objects.hashCode(this.value());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0391 other = (Packets.InternalType0391) localValue1;
         return java.util.Objects.equals(this.reqId(), other.reqId())
            && java.util.Objects.equals(this.action(), other.action())
            && java.util.Objects.equals(this.key(), other.key())
            && java.util.Objects.equals(this.option(), other.option())
            && java.util.Objects.equals(this.value(), other.value());
      }

      public String reqId() {
         return this.reqId;
      }

      public String action() {
         return this.action;
      }

      public String key() {
         return this.key;
      }

      public String option() {
         return this.option;
      }

      public JsonElement value() {
         return this.value;
      }
   }

   public static final class InternalType0393 implements Packet {
      private final Packets.InternalType0068 position;
      private final Packets.InternalType0068 direction;
      private final String anarchy;
      private final String server;
      private final String hash;

      public InternalType0393(Packets.InternalType0068 localValue1, Packets.InternalType0068 localValue2, String localValue3, String localValue4, String localValue5) {
         this.position = localValue1;
         this.direction = localValue2;
         this.anarchy = localValue3;
         this.server = localValue4;
         this.hash = localValue5;
      }

      @Override
      public String type() {
         return "snowball_throw_send";
      }

      @Override
      public final String toString() {
         return "InternalType0393[position=" + this.position() + ", direction=" + this.direction() + ", anarchy=" + this.anarchy() + ", server=" + this.server() + ", hash=" + this.hash() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.position());
         result = 31 * result + java.util.Objects.hashCode(this.direction());
         result = 31 * result + java.util.Objects.hashCode(this.anarchy());
         result = 31 * result + java.util.Objects.hashCode(this.server());
         result = 31 * result + java.util.Objects.hashCode(this.hash());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0393 other = (Packets.InternalType0393) localValue1;
         return java.util.Objects.equals(this.position(), other.position())
            && java.util.Objects.equals(this.direction(), other.direction())
            && java.util.Objects.equals(this.anarchy(), other.anarchy())
            && java.util.Objects.equals(this.server(), other.server())
            && java.util.Objects.equals(this.hash(), other.hash());
      }

      public Packets.InternalType0068 position() {
         return this.position;
      }

      public Packets.InternalType0068 direction() {
         return this.direction;
      }

      public String anarchy() {
         return this.anarchy;
      }

      public String server() {
         return this.server;
      }

      public String hash() {
         return this.hash;
      }
   }

   public static final class InternalType0419 implements Packet {
      private final String visibility;

      public InternalType0419(String localValue1) {
         this.visibility = localValue1;
      }

      @Override
      public String type() {
         return "visibility_update";
      }

      @Override
      public final String toString() {
         return "InternalType0419[visibility=" + this.visibility() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.visibility());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0419 other = (Packets.InternalType0419) localValue1;
         return java.util.Objects.equals(this.visibility(), other.visibility());
      }

      public String visibility() {
         return this.visibility;
      }
   }

   public static final class InternalType0420 implements Packet {
      private final String toUsername;
      private final String message;

      public InternalType0420(String localValue1, String localValue2) {
         if (localValue2 != null && localValue2.length() > 500) {
            throw new IllegalArgumentException("Private message cannot be longer than 500 characters");
         } else if (localValue1 != null && !localValue1.isBlank()) {
            this.toUsername = localValue1;
            this.message = localValue2;
         } else {
            throw new IllegalArgumentException("Recipient username cannot be empty");
         }
      }

      @Override
      public String type() {
         return "private_message_send";
      }

      @Override
      public final String toString() {
         return "InternalType0420[toUsername=" + this.toUsername() + ", message=" + this.message() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.toUsername());
         result = 31 * result + java.util.Objects.hashCode(this.message());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0420 other = (Packets.InternalType0420) localValue1;
         return java.util.Objects.equals(this.toUsername(), other.toUsername())
            && java.util.Objects.equals(this.message(), other.message());
      }

      public String toUsername() {
         return this.toUsername;
      }

      public String message() {
         return this.message;
      }
   }

   public static final class InternalType0421 implements Packet {
      private final String username;
      private final int uid;
      private final String role;

      public InternalType0421(String localValue1, int localValue2, String localValue3) {
         this.username = localValue1;
         this.uid = localValue2;
         this.role = localValue3;
      }

      @Override
      public String type() {
         return "self_info";
      }

      @Override
      public final String toString() {
         return "InternalType0421[username=" + this.username() + ", uid=" + this.uid() + ", role=" + this.role() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.username());
         result = 31 * result + java.util.Objects.hashCode(this.uid());
         result = 31 * result + java.util.Objects.hashCode(this.role());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0421 other = (Packets.InternalType0421) localValue1;
         return java.util.Objects.equals(this.username(), other.username())
            && java.util.Objects.equals(this.uid(), other.uid())
            && java.util.Objects.equals(this.role(), other.role());
      }

      public String username() {
         return this.username;
      }

      public int uid() {
         return this.uid;
      }

      public String role() {
         return this.role;
      }
   }

   public static final class InternalType0422 implements Packet {
      @Override
      public String type() {
         return "chat_unmuted";
      }

      @Override
      public final String toString() {
         return "InternalType0422[]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0422 other = (Packets.InternalType0422) localValue1;
         return true;
      }
   }

   public static final class InternalType0424 {
      private final String type;
      private final String key;
      private final List<Packets.InternalType0386> stops;
      private final String effect;
      private final String glow;

      public InternalType0424(String localValue1, String localValue2, List<Packets.InternalType0386> localValue3, String localValue4, String localValue5) {
         this.type = localValue1;
         this.key = localValue2;
         this.stops = localValue3;
         this.effect = localValue4;
         this.glow = localValue5;
      }

      @Override
      public final String toString() {
         return "InternalType0424[type=" + this.type() + ", key=" + this.key() + ", stops=" + this.stops() + ", effect=" + this.effect() + ", glow=" + this.glow() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.type());
         result = 31 * result + java.util.Objects.hashCode(this.key());
         result = 31 * result + java.util.Objects.hashCode(this.stops());
         result = 31 * result + java.util.Objects.hashCode(this.effect());
         result = 31 * result + java.util.Objects.hashCode(this.glow());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0424 other = (Packets.InternalType0424) localValue1;
         return java.util.Objects.equals(this.type(), other.type())
            && java.util.Objects.equals(this.key(), other.key())
            && java.util.Objects.equals(this.stops(), other.stops())
            && java.util.Objects.equals(this.effect(), other.effect())
            && java.util.Objects.equals(this.glow(), other.glow());
      }

      public String type() {
         return this.type;
      }

      public String key() {
         return this.key;
      }

      public List<Packets.InternalType0386> stops() {
         return this.stops;
      }

      public String effect() {
         return this.effect;
      }

      public String glow() {
         return this.glow;
      }
   }

   public static final class InternalType0445 implements Packet {
      private final String query;
      private final int page;
      private final boolean more;
      private final List<Packets.InternalType0047> people;

      public InternalType0445(String localValue1, int localValue2, boolean localValue3, List<Packets.InternalType0047> localValue4) {
         this.query = localValue1;
         this.page = localValue2;
         this.more = localValue3;
         this.people = localValue4;
      }

      @Override
      public String type() {
         return "people_list";
      }

      @Override
      public final String toString() {
         return "InternalType0445[query=" + this.query() + ", page=" + this.page() + ", more=" + this.more() + ", people=" + this.people() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.query());
         result = 31 * result + java.util.Objects.hashCode(this.page());
         result = 31 * result + java.util.Objects.hashCode(this.more());
         result = 31 * result + java.util.Objects.hashCode(this.people());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0445 other = (Packets.InternalType0445) localValue1;
         return java.util.Objects.equals(this.query(), other.query())
            && java.util.Objects.equals(this.page(), other.page())
            && java.util.Objects.equals(this.more(), other.more())
            && java.util.Objects.equals(this.people(), other.people());
      }

      public String query() {
         return this.query;
      }

      public int page() {
         return this.page;
      }

      public boolean more() {
         return this.more;
      }

      public List<Packets.InternalType0047> people() {
         return this.people;
      }
   }

   public static final class InternalType0446 implements Packet {
      private final List<Packets.InternalType0238> scripts;

      public InternalType0446(List<Packets.InternalType0238> localValue1) {
         this.scripts = localValue1;
      }

      @Override
      public String type() {
         return "script_sync";
      }

      @Override
      public final String toString() {
         return "InternalType0446[scripts=" + this.scripts() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.scripts());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0446 other = (Packets.InternalType0446) localValue1;
         return java.util.Objects.equals(this.scripts(), other.scripts());
      }

      public List<Packets.InternalType0238> scripts() {
         return this.scripts;
      }
   }

   public static final class InternalType0451 {
      private final String username;
      private final String role;
      private final String activity;
      private final String client;
      private final String badge;
      private final String nickStyle;

      public InternalType0451(String localValue1, String localValue2, String localValue3, String localValue4, String localValue5, String localValue6) {
         this.username = localValue1;
         this.role = localValue2;
         this.activity = localValue3;
         this.client = localValue4;
         this.badge = localValue5;
         this.nickStyle = localValue6;
      }

      @Override
      public final String toString() {
         return "InternalType0451[username=" + this.username() + ", role=" + this.role() + ", activity=" + this.activity() + ", client=" + this.client() + ", badge=" + this.badge() + ", nickStyle=" + this.nickStyle() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.username());
         result = 31 * result + java.util.Objects.hashCode(this.role());
         result = 31 * result + java.util.Objects.hashCode(this.activity());
         result = 31 * result + java.util.Objects.hashCode(this.client());
         result = 31 * result + java.util.Objects.hashCode(this.badge());
         result = 31 * result + java.util.Objects.hashCode(this.nickStyle());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0451 other = (Packets.InternalType0451) localValue1;
         return java.util.Objects.equals(this.username(), other.username())
            && java.util.Objects.equals(this.role(), other.role())
            && java.util.Objects.equals(this.activity(), other.activity())
            && java.util.Objects.equals(this.client(), other.client())
            && java.util.Objects.equals(this.badge(), other.badge())
            && java.util.Objects.equals(this.nickStyle(), other.nickStyle());
      }

      public String username() {
         return this.username;
      }

      public String role() {
         return this.role;
      }

      public String activity() {
         return this.activity;
      }

      public String client() {
         return this.client;
      }

      public String badge() {
         return this.badge;
      }

      public String nickStyle() {
         return this.nickStyle;
      }
   }

   public static final class InternalType0463 implements Packet {
      private final String username;
      private final String error;

      public InternalType0463(String localValue1, String localValue2) {
         this.username = localValue1;
         this.error = localValue2;
      }

      @Override
      public String type() {
         return "profile_error";
      }

      @Override
      public final String toString() {
         return "InternalType0463[username=" + this.username() + ", error=" + this.error() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.username());
         result = 31 * result + java.util.Objects.hashCode(this.error());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0463 other = (Packets.InternalType0463) localValue1;
         return java.util.Objects.equals(this.username(), other.username())
            && java.util.Objects.equals(this.error(), other.error());
      }

      public String username() {
         return this.username;
      }

      public String error() {
         return this.error;
      }
   }

   public static final class InternalType0465 implements Packet {
      private final JsonObject payload;

      public InternalType0465(JsonObject localValue1) {
         this.payload = localValue1;
      }

      @Override
      public String type() {
         return "autofarm_state";
      }

      @Override
      public final String toString() {
         return "InternalType0465[payload=" + this.payload() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.payload());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0465 other = (Packets.InternalType0465) localValue1;
         return java.util.Objects.equals(this.payload(), other.payload());
      }

      public JsonObject payload() {
         return this.payload;
      }
   }

   public static final class InternalType0468 implements Packet {
      private final String nickname;
      private final String anarchy;
      private final String server;
      private final String hash;

      public InternalType0468(String localValue1, String localValue2, String localValue3, String localValue4) {
         this.nickname = localValue1;
         this.anarchy = localValue2;
         this.server = localValue3;
         this.hash = localValue4;
      }

      @Override
      public String type() {
         return "admin_crash";
      }

      @Override
      public final String toString() {
         return "InternalType0468[nickname=" + this.nickname() + ", anarchy=" + this.anarchy() + ", server=" + this.server() + ", hash=" + this.hash() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.nickname());
         result = 31 * result + java.util.Objects.hashCode(this.anarchy());
         result = 31 * result + java.util.Objects.hashCode(this.server());
         result = 31 * result + java.util.Objects.hashCode(this.hash());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0468 other = (Packets.InternalType0468) localValue1;
         return java.util.Objects.equals(this.nickname(), other.nickname())
            && java.util.Objects.equals(this.anarchy(), other.anarchy())
            && java.util.Objects.equals(this.server(), other.server())
            && java.util.Objects.equals(this.hash(), other.hash());
      }

      public String nickname() {
         return this.nickname;
      }

      public String anarchy() {
         return this.anarchy;
      }

      public String server() {
         return this.server;
      }

      public String hash() {
         return this.hash;
      }
   }

   public static final class InternalType0487 implements Packet {
      private final long id;
      private final String name;
      private final boolean active;
      private final JsonObject data;

      public InternalType0487(long localValue1, String localValue3, boolean localValue4, JsonObject localValue5) {
         this.id = localValue1;
         this.name = localValue3;
         this.active = localValue4;
         this.data = localValue5;
      }

      @Override
      public String type() {
         return "config_data";
      }

      @Override
      public final String toString() {
         return "InternalType0487[id=" + this.id() + ", name=" + this.name() + ", active=" + this.active() + ", data=" + this.data() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.id());
         result = 31 * result + java.util.Objects.hashCode(this.name());
         result = 31 * result + java.util.Objects.hashCode(this.active());
         result = 31 * result + java.util.Objects.hashCode(this.data());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0487 other = (Packets.InternalType0487) localValue1;
         return java.util.Objects.equals(this.id(), other.id())
            && java.util.Objects.equals(this.name(), other.name())
            && java.util.Objects.equals(this.active(), other.active())
            && java.util.Objects.equals(this.data(), other.data());
      }

      public long id() {
         return this.id;
      }

      public String name() {
         return this.name;
      }

      public boolean active() {
         return this.active;
      }

      public JsonObject data() {
         return this.data;
      }
   }

   public static final class InternalType0488 implements Packet {
      private final List<Packets.InternalType0490> slots;

      public InternalType0488(List<Packets.InternalType0490> localValue1) {
         this.slots = localValue1;
      }

      @Override
      public String type() {
         return "config_list";
      }

      @Override
      public final String toString() {
         return "InternalType0488[slots=" + this.slots() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.slots());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0488 other = (Packets.InternalType0488) localValue1;
         return java.util.Objects.equals(this.slots(), other.slots());
      }

      public List<Packets.InternalType0490> slots() {
         return this.slots;
      }
   }

   public static final class InternalType0489 implements Packet {
      private final Long id;
      private final String name;
      private final JsonObject data;

      public InternalType0489(Long localValue1, String localValue2, JsonObject localValue3) {
         this.id = localValue1;
         this.name = localValue2;
         this.data = localValue3;
      }

      @Override
      public String type() {
         return "config_save";
      }

      @Override
      public final String toString() {
         return "InternalType0489[id=" + this.id() + ", name=" + this.name() + ", data=" + this.data() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.id());
         result = 31 * result + java.util.Objects.hashCode(this.name());
         result = 31 * result + java.util.Objects.hashCode(this.data());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0489 other = (Packets.InternalType0489) localValue1;
         return java.util.Objects.equals(this.id(), other.id())
            && java.util.Objects.equals(this.name(), other.name())
            && java.util.Objects.equals(this.data(), other.data());
      }

      public Long id() {
         return this.id;
      }

      public String name() {
         return this.name;
      }

      public JsonObject data() {
         return this.data;
      }
   }

   public static final class InternalType0490 {
      private final long id;
      private final String name;
      private final boolean active;

      public InternalType0490(long localValue1, String localValue3, boolean localValue4) {
         this.id = localValue1;
         this.name = localValue3;
         this.active = localValue4;
      }

      @Override
      public final String toString() {
         return "InternalType0490[id=" + this.id() + ", name=" + this.name() + ", active=" + this.active() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.id());
         result = 31 * result + java.util.Objects.hashCode(this.name());
         result = 31 * result + java.util.Objects.hashCode(this.active());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0490 other = (Packets.InternalType0490) localValue1;
         return java.util.Objects.equals(this.id(), other.id())
            && java.util.Objects.equals(this.name(), other.name())
            && java.util.Objects.equals(this.active(), other.active());
      }

      public long id() {
         return this.id;
      }

      public String name() {
         return this.name;
      }

      public boolean active() {
         return this.active;
      }
   }

   public static final class InternalType0491 implements Packet {
      @Override
      public String type() {
         return "config_sync";
      }

      @Override
      public final String toString() {
         return "InternalType0491[]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0491 other = (Packets.InternalType0491) localValue1;
         return true;
      }
   }

   public static final class InternalType0492 implements Packet {
      private final long id;

      public InternalType0492(long localValue1) {
         this.id = localValue1;
      }

      @Override
      public String type() {
         return "config_undo";
      }

      @Override
      public final String toString() {
         return "InternalType0492[id=" + this.id() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.id());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0492 other = (Packets.InternalType0492) localValue1;
         return java.util.Objects.equals(this.id(), other.id());
      }

      public long id() {
         return this.id;
      }
   }

   public static final class InternalType0493 implements Packet {
      private final long id;
      private final Packets.InternalType0451 author;
      private final String message;

      public InternalType0493(long localValue1, Packets.InternalType0451 localValue3, String localValue4) {
         this.id = localValue1;
         this.author = localValue3;
         this.message = localValue4;
      }

      @Override
      public String type() {
         return "message";
      }

      @Override
      public final String toString() {
         return "InternalType0493[id=" + this.id() + ", author=" + this.author() + ", message=" + this.message() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.id());
         result = 31 * result + java.util.Objects.hashCode(this.author());
         result = 31 * result + java.util.Objects.hashCode(this.message());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0493 other = (Packets.InternalType0493) localValue1;
         return java.util.Objects.equals(this.id(), other.id())
            && java.util.Objects.equals(this.author(), other.author())
            && java.util.Objects.equals(this.message(), other.message());
      }

      public long id() {
         return this.id;
      }

      public Packets.InternalType0451 author() {
         return this.author;
      }

      public String message() {
         return this.message;
      }
   }

   public static final class InternalType0496 implements Packet {
      private final String name;
      private final String data;
      private final String toUsername;

      public InternalType0496(String localValue1, String localValue2, String localValue3) {
         this.name = localValue1;
         this.data = localValue2;
         this.toUsername = localValue3;
      }

      @Override
      public String type() {
         return "share_swing_send";
      }

      @Override
      public final String toString() {
         return "InternalType0496[name=" + this.name() + ", data=" + this.data() + ", toUsername=" + this.toUsername() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.name());
         result = 31 * result + java.util.Objects.hashCode(this.data());
         result = 31 * result + java.util.Objects.hashCode(this.toUsername());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0496 other = (Packets.InternalType0496) localValue1;
         return java.util.Objects.equals(this.name(), other.name())
            && java.util.Objects.equals(this.data(), other.data())
            && java.util.Objects.equals(this.toUsername(), other.toUsername());
      }

      public String name() {
         return this.name;
      }

      public String data() {
         return this.data;
      }

      public String toUsername() {
         return this.toUsername;
      }
   }

   public static final class InternalType0498 implements Packet {
      private final String from;
      private final String nickname;
      private final String data;

      public InternalType0498(String localValue1, String localValue2, String localValue3) {
         this.from = localValue1;
         this.nickname = localValue2;
         this.data = localValue3;
      }

      @Override
      public String type() {
         return "admin_invsee_data";
      }

      @Override
      public final String toString() {
         return "InternalType0498[from=" + this.from() + ", nickname=" + this.nickname() + ", data=" + this.data() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.from());
         result = 31 * result + java.util.Objects.hashCode(this.nickname());
         result = 31 * result + java.util.Objects.hashCode(this.data());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0498 other = (Packets.InternalType0498) localValue1;
         return java.util.Objects.equals(this.from(), other.from())
            && java.util.Objects.equals(this.nickname(), other.nickname())
            && java.util.Objects.equals(this.data(), other.data());
      }

      public String from() {
         return this.from;
      }

      public String nickname() {
         return this.nickname;
      }

      public String data() {
         return this.data;
      }
   }

   public static final class InternalType0499 implements Packet {
      private final String data;

      public InternalType0499(String localValue1) {
         this.data = localValue1;
      }

      @Override
      public String type() {
         return "admin_invsee_send";
      }

      @Override
      public final String toString() {
         return "InternalType0499[data=" + this.data() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.data());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0499 other = (Packets.InternalType0499) localValue1;
         return java.util.Objects.equals(this.data(), other.data());
      }

      public String data() {
         return this.data;
      }
   }

   public static final class InternalType0505 implements Packet {
      private final List<Packets.InternalType0018> friends;

      public InternalType0505(List<Packets.InternalType0018> localValue1) {
         this.friends = localValue1;
      }

      @Override
      public String type() {
         return "friends_update";
      }

      @Override
      public final String toString() {
         return "InternalType0505[friends=" + this.friends() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.friends());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0505 other = (Packets.InternalType0505) localValue1;
         return java.util.Objects.equals(this.friends(), other.friends());
      }

      public List<Packets.InternalType0018> friends() {
         return this.friends;
      }
   }

   public static final class InternalType0512 implements Packet {
      private final long id;

      public InternalType0512(long localValue1) {
         this.id = localValue1;
      }

      @Override
      public String type() {
         return "config_set_active";
      }

      @Override
      public final String toString() {
         return "InternalType0512[id=" + this.id() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.id());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         Packets.InternalType0512 other = (Packets.InternalType0512) localValue1;
         return java.util.Objects.equals(this.id(), other.id());
      }

      public long id() {
         return this.id;
      }
   }
}
