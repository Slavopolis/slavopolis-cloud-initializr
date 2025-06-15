import AxiosMockAdapter from 'axios-mock-adapter';
import axios from '../utils/axios.ts';

const mock = new AxiosMockAdapter(axios, { delayResponse: 0 });
export default mock;
