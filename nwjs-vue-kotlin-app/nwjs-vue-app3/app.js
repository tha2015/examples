
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
    		  const kotlinapp = require ('./kotlinapp.js');
    		  const hello = new kotlinapp.Hello()
    		  alert(hello.sayHello("John"));
      }
    }
  });