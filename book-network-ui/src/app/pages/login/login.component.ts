import { Component } from '@angular/core';
import {AuthenticateRequest} from '../../services/models/authenticate-request';
import {AuthenticationService} from '../../services/services/authentication.service';
import {Router} from '@angular/router';
import {TokenService} from '../../services/token/token.service';
import {RegistrationRequest} from '../../services/models/registration-request';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  authRequest: AuthenticateRequest = {email: '', password: ''};
  registerRequest: RegistrationRequest = { email: "", firstname: '', lastname: "", password: ''};

  errorMsg: Array<string> = [];
  successMsg: string = '';

  constructor(
    private authenticationService: AuthenticationService,
    private router: Router,
    private tokenService: TokenService
  ) {}

  login() {
    this.errorMsg = [];
    this.authenticationService.authenticate( {
        body: this.authRequest
      }
    ).subscribe({
      next: result => {
        this.tokenService.setToken(result.token as string);
        this.router.navigate(['books'])
      },
      error: err => {
        console.log(err);

        if (err.error.validationErrors) {
          this.errorMsg.push(...err.error.validationErrors);
        } else {
          this.errorMsg.push(err.error.error);
        }
      }
    })
  }

  signUp()  {
    this.errorMsg = [];
    this.authenticationService.register( {
        body: this.registerRequest
      }
    ).subscribe({
      next: result => {
        this.router.navigate(['activate-account'])
        this.successMsg = "Registration was successfully check your email";
      },
      error: err => {
        console.log(err);

        if (err.error.validationErrors) {
          this.errorMsg.push(...err.error.validationErrors);
        } else {
          this.errorMsg.push(err.error.error);
        }
      }
    })
  }
}
