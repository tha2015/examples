declare var Vue:any; // Magic
declare var VueMaterial:any; // Magic


const app = require ('typescriptapp');
//import { sayHello as sayHello} from "typescriptapp";

Vue.use(VueMaterial);

var App = new Vue({
el: '#app',
data: {
    firstName: '',
    lastName: '',
    log: ''
},
methods: {
        onSubmit: function () {
            alert(app.sayHello("John"));
    }
}
});