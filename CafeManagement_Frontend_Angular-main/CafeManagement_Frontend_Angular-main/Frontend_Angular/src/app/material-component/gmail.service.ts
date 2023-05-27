import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GmailService {

  gmailData:any;

  constructor() { }


  getDataFromForgotPassword(data:any)
  {
    this.gmailData=data;
    console.log(data);
  }


  sendDataToBackend(data:any)
  {
    console.log(data);
  }


}
