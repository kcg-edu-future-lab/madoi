import { createContext } from "react";
import { Madoi } from "./madoi/madoi";
import { Chat, newChatAndOpts } from "./Chat";

export class MadoiObjects{
  madoi: Madoi;
  chat: Chat;

  constructor(){
    const m = new Madoi(
      "ws://localhost:8080/madoi/rooms/chat_by_object_ts_react_eoai24rJwke",
      "ahfuTep6ooDi7Oa4");
    this.madoi = m;
    // vite5.3.3ではexperimentalDecoratorsが正常に動作しないため暫定措置。
    // 動作するようになれば、オブジェクトの共有設定はデコレータで指定する。
    this.chat = m.register(...newChatAndOpts());
  }
}

export const MadoiContext = createContext(new MadoiObjects());
