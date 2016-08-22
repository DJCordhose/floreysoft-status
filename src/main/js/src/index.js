import './index.css';

import injectTapEventPlugin from 'react-tap-event-plugin';
// Needed for onTouchTap
// http://stackoverflow.com/a/34015469/988941
injectTapEventPlugin();

// https://github.com/webpack/expose-loader
import 'expose?init!./api-init';
