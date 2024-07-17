import { createContext } from "react";
import { GetState, Madoi, SetState, Share } from "./madoi/madoi";
import { SharedObject } from "./madoi/helpers";

export class MadoiObjects{
  madoi: Madoi;
  chat: Chat;

  constructor(){
    const m = new Madoi(
      "ws://localhost:8080/madoi/rooms/chat_by_object_ts_react_eoai24rJwke",
      "ahfuTep6ooDi7Oa4");
    this.madoi = m;
    this.chat = m.register(new Chat());
  }
}

export class Chat extends SharedObject<Chat>{
  private logs: string[] = [];

  @Share()
  addLog(log: string){
    this.logs = [...this.logs, log];
    this.fireChange(this.logs);
  }

  @GetState()
  getState(){
    return this.logs;
  }

  @SetState()
  setState(logs: string[]){
    this.logs = logs;
    this.fireChange(this.logs);
  }
}

export const MadoiContext = createContext(new MadoiObjects());
