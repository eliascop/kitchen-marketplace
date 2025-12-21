import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment.dev';
import { DataService } from './data.service';
import { ServiceResponse } from './model/http-options-request.model';
import { CepResponse } from '../model/cep.model';

export const CEP_SERVICE_WS = environment.CEP_REST_SERVICE;

@Injectable({ providedIn: 'root' })
export class CepService {

  constructor(private dataService: DataService) {}
        
  getAddressByCEP(cep:String): ServiceResponse<CepResponse> {
    return this.dataService.get<CepResponse>({
      url: `${CEP_SERVICE_WS}/${cep}`
    });
  }

}
