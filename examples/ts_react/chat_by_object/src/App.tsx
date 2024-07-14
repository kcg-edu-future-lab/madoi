import { FormEventHandler, useContext, useRef } from 'react'
import './App.css'
import { MadoiContext } from './MadoiContext';
import { useSharedObject } from './madoi/helpers';

function App() {
  const m = useContext(MadoiContext);
  const inputRef = useRef<HTMLInputElement>(null);
  const logs: string[] = useSharedObject(m.chat);

  const onSubmit: FormEventHandler = e=>{
    e.preventDefault();
    const input = inputRef.current;
    if(input === null || !input.value) return;
    m.chat.addLog(input.value);
    input.value = "";
  };

  return <>
    <form onSubmit={onSubmit}>
      <label>message:
        <input ref={inputRef} type="text" autoFocus placeholder="enter to send" />
      </label>
    </form>
    <div id="log">{logs.map((l, i)=>
      <div key={i}>{l}</div>
    )}</div>
  </>;
}

export default App
