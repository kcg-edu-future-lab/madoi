import { FormEventHandler, useEffect, useRef, useState } from 'react'
import './App.css'
import { Madoi } from './madoi/madoi';

function App() {
  const [logs, setLogs] = useState<string[]>([]);
  const input = useRef<HTMLInputElement>(null);
  const madoi = useRef<Madoi>();

  useEffect(()=>{
    if(!madoi.current){
      madoi.current = new Madoi(
        "ws://localhost:8080/madoi/rooms/chat_by_sendrecv_ts_oii24Jwke",
        "ahfuTep6ooDi7Oa4");
      madoi.current.addReceiver<string>("chat", ({detail: {content}})=>{
        setLogs(logs=>[...logs, content]);
      });
    }
  }, []);
  const onSubmit: FormEventHandler = e=>{
    e.preventDefault();
    if(input.current === null || !input.current.value || !madoi.current) return;
    madoi.current.send("chat", input.current.value);
    input.current.value = "";
  };

  return <>
    <form onSubmit={onSubmit}>
      <label>message:
        <input ref={input} type="text" autoFocus placeholder="enter to send" />
      </label>
    </form>
    <div id="log">{logs.map((l, i)=>
      <div key={i}>{l}</div>
    )}</div>
  </>;
}

export default App
