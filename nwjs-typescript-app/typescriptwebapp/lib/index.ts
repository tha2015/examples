declare var Vue:any; // Magic
declare var VueMaterial:any; // Magic


import { sayHello as sayHello} from "typescriptapp";

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
            alert(sayHello("John"));
    }
}
});