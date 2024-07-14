import { useEffect, useState } from "react";
import { TypedEventTarget } from "./madoi";

export abstract class SharedObject<T extends TypedEventTarget<T>> extends TypedEventTarget<T>{
    abstract getState(): any;
    fireChange(detail: any){
        this.dispatchEvent(new CustomEvent("change", {detail}));
    }
}

export function useSharedObject<T extends TypedEventTarget<T>, U>(
    obj: SharedObject<T>): U{
    const [state, setState] = useState<U>(obj.getState());
    const handler = (e: CustomEvent)=>{
        setState(e.detail);
    }
    useEffect(()=>{
        obj.addEventListener("change", handler as EventListener);
        return ()=>{
            obj.removeEventListener("change", handler as EventListener);
        };
    }, []);
    return state;
}
