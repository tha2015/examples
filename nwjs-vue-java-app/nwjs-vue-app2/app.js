
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
    		  const cal = require ('./javaapp.js')
    		  alert(cal(10, {
    			accept:  function(r) {alert('r=' + r)}
    		  	}
    		  ));


          alert('Writing to file form.txt your name: ' + this.firstName + ' ' + this.lastName);

          var fs = require('fs')
          var logger = fs.createWriteStream('form.txt', {
            flags: 'a' // 'a' means appending (old data will be preserved)
          })
          logger.write('First Name: ' + this.firstName + '; Last Name: ' + this.lastName) // append string to your file
          logger.end();

          this.log += '\n' + 'First Name: ' + this.firstName + '; Last Name: ' + this.lastName;

          var _this = this;

          const { exec } = require('child_process');
          const ls = exec('ls', ['-lh', '/']);

          ls.stdout.on('data', (data) => {
            console.log(`stdout: ${data}`);
            _this.log += data;
          });

          ls.stderr.on('data', (data) => {
            console.log(`stderr: ${data}`);
            _this.log += data;
          });

          ls.on('close', (code) => {
            console.log(`child process exited with code ${code}`);
            _this.log += "exit: " + code;
          });
      }
    }
  });