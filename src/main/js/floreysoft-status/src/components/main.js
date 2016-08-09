import React, {Component} from 'react';
import './main.css';

// http://v4-alpha.getbootstrap.com/
export default class Main extends Component {
    render() {
        return (
            <div>
                <div className="navbar navbar-inverse navbar-fixed-top">
                    <div className="navbar-inner">
                        <div className="container">
                            <button type="button" className="btn btn-navbar" data-toggle="collapse"
                                    data-target=".nav-collapse">
                                <span className="icon-bar"></span>
                                <span className="icon-bar"></span>
                                <span className="icon-bar"></span>
                            </button>
                            <a className="brand" href="#">Hello Endpoints!</a>
                            <div className="nav-collapse collapse pull-right">
                                <a href="javascript:void(0);" className="btn" id="signinButton">Sign in</a>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="container">

                    <div id="outputLog"></div>

                    <form action="javascript:void(0);">
                        <h2>Get Greeting</h2>
                        <div><span style={{
                            width: "90px",
                            display: "inline-block"
                        }}>Greeting ID: </span><input id="id"/>
                        </div>
                        <div><input id="getGreeting" type="submit" className="btn btn-small" value="Submit"/></div>
                    </form>

                    <form action="javascript:void(0);">
                        <h2>List Greetings</h2>
                        <div><input id="listGreeting" type="submit" className="btn btn-small" value="Submit"/></div>
                    </form>

                    <form action="javascript:void(0);">
                        <h2>Multiply Greetings</h2>
                        <div><span style={{
                            width: "90px",
                            display: "inline-block"
                        }}>Greeting: </span><input id="greeting"/>
                        </div>
                        <div><span style={{
                            width: "90px",
                            display: "inline-block"
                        }}>Count: </span><input id="count"/></div>
                        <div><input id="multiplyGreetings" type="submit" className="btn btn-small" value="Submit"/>
                        </div>
                    </form>

                    <form action="javascript:void(0);">
                        <h2>Authenticated Greeting</h2>
                        <div><input id="authedGreeting" type="submit" className="btn btn-small" disabled
                                    value="Submit"/>
                        </div>
                    </form>
                </div>
            </div>);
    }
}

