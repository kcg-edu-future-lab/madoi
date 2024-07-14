import { SharedObject } from './madoi/helpers';
import { MethodAndConfigParam } from './madoi/madoi';

export class Chat extends SharedObject<Chat>{
    private logs: string[] = [];
  
    addLog(log: string){
      this.logs = [...this.logs, log];
      this.fireChange(this.logs);
    }

    override getState(){
      return this.logs;
    }

    setState(logs: string[]){
      this.logs = logs;
      this.fireChange(this.logs);
    }
}

export function newChatAndOpts(): [Chat, MethodAndConfigParam[]]{
  const c = new Chat();
  return [
    c, [
        {method: c.addLog, share: {}},
        {method: c.getState, getState: {}},
        {method: c.setState, setState: {}}
    ]];
};
