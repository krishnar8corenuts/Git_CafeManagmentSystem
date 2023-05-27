import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { UserService } from 'src/app/services/user.service';
import { GlobalConstants } from 'src/app/shared/global-constants';
import { OtpFormComponent } from '../otp-form/otp-form.component';
import { GmailService } from '../gmail.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {

  constructor(
    private gmailService:GmailService,
    private dialog:MatDialog,
    private formBuilder: FormBuilder,
    private router: Router,
    private snackbarService: SnackbarService,
    private ngxService: NgxUiLoaderService,
    private userservice: UserService,
    public dialogRef: MatDialogRef<OtpFormComponent>

  ) { }

  hide = true;
  forgotPassword: any = FormGroup;
  responseMessage: any;

  ngOnInit(): void {

    this.forgotPassword = this.formBuilder.group({
      email: [
        null,
        [Validators.required, Validators.pattern(GlobalConstants.emialRegex)]]
    });
  }

  handleSubmit(){

    console.log("Submitted");
    this.ngxService.start();
    var formData = this.forgotPassword.value;
    var data = {
      email: formData.email,
    };

    this.gmailService.getDataFromForgotPassword(data.email);

    this.userservice.forgotPassword(data).subscribe(
      (response: any) => {
        console.log(response);
        if(response.message=="Check Your Mail For OTP")
        {
          this.responseMessage = 'Check Your Mail For OTP';
          this.snackbarService.openSnackBar(this.responseMessage, '');
          this.handleOtpAction();
        }
        else{
          this.responseMessage = 'Email Does not Exist';
          this.snackbarService.openSnackBar(this.responseMessage, '');
          this.router.navigate(['/']);
        }

        this.ngxService.stop();
        this.dialogRef.close();
      },
      (error) => {
        this.ngxService.stop();
        if (error.error?.message) {
          this.responseMessage = error.error?.message;
        } else {
          this.responseMessage = GlobalConstants.genricError;
        }
        this.snackbarService.openSnackBar(
          this.responseMessage,
          GlobalConstants.error
          );
      }
    );
  }


  handleOtpAction()
  {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width="550px";
    this.dialog.open(OtpFormComponent,dialogConfig);
  }
}
