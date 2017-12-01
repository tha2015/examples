import {writeFileSync as writeFileSync} from 'fs'


export function sayHello(str: string): string {
    writeFileSync("./hello.txt", "Hello " + str);
    return 'Hello ' + str;
}