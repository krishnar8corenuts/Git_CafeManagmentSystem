export const environment = {

  production: false,

  authurl:'http://localhost:9021',//for authentication service
  apiurl:'http://localhost:9022',//for inventory

    prodauturl:'http://34.122.139.85:9021',//for authentication service
  prodapiurl:'http://34.122.139.85:9022', //for inventory,


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


