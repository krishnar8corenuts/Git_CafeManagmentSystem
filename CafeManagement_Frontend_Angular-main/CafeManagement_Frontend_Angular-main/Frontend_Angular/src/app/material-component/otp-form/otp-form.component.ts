import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { UserService } from 'src/app/services/user.service';
import { MatDialogRef } from '@angular/material/dialog';
import { GlobalConstants } from 'src/app/shared/global-constants';
import { GmailService } from '../gmail.service';

@Component({
  selector: 'app-otp-form',
  templateUrl: './otp-form.component.html',
  styleUrls: ['./otp-form.component.scss']
})
export class OtpFormComponent implements OnInit {


  constructor(
    private gmailService:GmailService,
    private formBuilder: FormBuilder,
    private router: Router,
    private snackbarService: SnackbarService,
    private ngxService: NgxUiLoaderService,
    private userservice: UserService,
    public dialogRef: MatDialogRef<OtpFormComponent>

  ) { }

  hide = true;
  otpDetails: any = FormGroup;
  responseMessage: any;
  emailData:any="";

  ngOnInit(): void {

    this.displayGmailData()

    this.otpDetails = this.formBuilder.group({
     otp:[null,[Validators.required,Validators.pattern(GlobalConstants.otpRegex)]],
    });
  }

  handleSubmit()
  {
    console.log("Submitted");
    // this.ngxService.start();
    var formData = this.otpDetails.value;
    var data = {
      otp: formData.otp,
      email:this.emailData
    };
    console.log(data);

    // this.userservice.sendDataToBackend(data)

  }

  displayGmailData()
  {
   this.emailData=this.gmailService.gmailData;
   console.log(this.emailData);
  }
}
