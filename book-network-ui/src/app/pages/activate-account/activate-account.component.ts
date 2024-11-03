import { Component } from '@angular/core';
import {Router} from '@angular/router';
import {AuthenticationService} from '../../services/services/authentication.service';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrl: './activate-account.component.scss'
})
export class ActivateAccountComponent {

  constructor(private authenticationService: AuthenticationService, private router: Router) {
  }

  activateErrorMsg: string = '';
  isSubmitted: boolean = false;
  errorExist: boolean = false;

  activateAccount(token: string) {
    this.authenticationService.confirm({ token }).subscribe({
      next: () => {
        console.log("Account activated successfully");
        this.activateErrorMsg = 'Your account has been successfully activated.\nNow you can proceed to login';
        this.isSubmitted = true;
        this.errorExist = false
      },
      error: (err: HttpErrorResponse) => {
        console.log(err);

        this.activateErrorMsg = err.error?.businessErrorDescription
          ? err.error.businessErrorDescription
          : 'An error occurred during activation. Please try again.';

        this.errorExist = true;
        console.log(this.errorExist);
        console.log("Activation error");
      }
    });
  }


  redirectToLogin() {
    this.router.navigate(['login']);
  }
}
