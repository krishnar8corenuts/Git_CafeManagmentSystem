export const environment = {

  production: false,

  authurl:'http://localhost:9021',//for authentication service
  apiurl:'http://localhost:9022',//for inventory

  prodauturl:'http://10.8.9.10:9021',//for authentication service
  prodapiurl:'http://10.8.9.10:9021', //for inventory,


  getApiUrl(data:string)
  {
   if(environment.production){
    if(data=='auth'){
      return environment.prodauturl;
    }else{
      return environment.prodapiurl;
    }
   }
   else{
    if(data=='api'){
      return environment.apiurl;
    }else{
      return environment.authurl;
    }
   }
  }

};


