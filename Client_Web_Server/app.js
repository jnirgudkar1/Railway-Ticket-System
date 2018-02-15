/**
 * Module dependencies.
 */

var express = require('express'),
    routes = require('./routes'),
    user = require('./routes/user'),
    http = require('http'),
    path = require('path');

var passport = require('passport');
var Strategy = require('passport-facebook').Strategy;
var GoogleStrategy = require('passport-google-oauth20').Strategy;
var app = express();

// all environments
app.set('port', process.env.PORT || 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(require('body-parser').urlencoded({
    extended: true
}));
app.use(express.methodOverride());

app.use(express.static(path.join(__dirname, 'public')));
app.use(require('express-session')({
    secret: 'keyboard cat',
    resave: true,
    saveUninitialized: true
}));
// development only
if ('development' == app.get('env')) {
    app.use(express.errorHandler());
}

passport.use(new Strategy({
        clientID: "2017812735158937",
        clientSecret: "06d2e809992e0e3a2a9c940d23ff2378",
        callbackURL: 'http://localhost:3000/login/facebook/return',
        profileFields: ['id', 'emails', 'name']
    },
    function (accessToken, refreshToken, profile, cb) {
        console.log("Testing facebook", profile.emails[0].value);
        console.log("testing google", profile.name.familyName);
        console.log("testing google", profile.name.givenName);
        var options = {
            host: "10.0.0.73",
            port: 8080,
            path: "/registerNewUser?firstName=" + profile.name.givenName + "&lastName=" + profile.name.familyName + "&email=" + profile.emails[0].value + "&password=default",
            method: 'POST'
        };
        http.request(options, function (res) {
            console.log('STATUS: ' + res.statusCode);
        }).end();

        return cb(null, profile);
    }));

passport.use(new GoogleStrategy({
        clientID: "174611181935-3qajb6vd20ge96uoj2rth7s75lrd7tt6.apps.googleusercontent.com",
        clientSecret: "GS-gcuro4FJiiO63Ak1osTiY",
        callbackURL: "http://localhost:3000/auth/google/callback"
    },
    function (accessToken, refreshToken, profile, cb) {
        console.log("testing google", profile.name.familyName);
        console.log("testing google", profile.name.givenName);
        var options = {
            host: "10.0.0.73",
            port: 8080,
            path: "/registerNewUser?firstName=" + profile.name.givenName + "&lastName=" + profile.name.familyName + "&email=" + profile.emails[0].value + "&password=default",
            method: 'POST'
        };
        http.request(options, function (res) {
            console.log('STATUS: ' + res.statusCode);
        }).end();

        return cb(null, profile);
    }
));

passport.serializeUser(function (user, cb) {
    cb(null, user);
});

passport.deserializeUser(function (obj, cb) {
    cb(null, obj);
});

app.use(passport.initialize());
app.use(passport.session());
app.use(app.router);

app.get('/login/facebook', passport.authenticate('facebook', {
    scope: ['email']
}));
app.get('/login/facebook/return',
    passport.authenticate('facebook', {
        failureRedirect: '/'
    }),
    function (req, res) {
        res.redirect('/home');
    });

app.get('/auth/google',
    passport.authenticate('google', {
        scope: ['https://www.googleapis.com/auth/userinfo.profile', 'https://www.googleapis.com/auth/userinfo.email']
    }));

app.get('/auth/google/callback',
    passport.authenticate('google', {
        failureRedirect: '/'
    }),
    function (req, res) {
        // Successful authentication, redirect home.
        res.redirect('/home');
    });

app.get('/', routes.index);
app.get('/users', user.list);
app.get('/home',
    function (req, res) {
        res.render('home');
    });
app.get('/admin',
    function (req, res) {
        res.render('admin');
    });
app.get('/admin_home',
    function (req, res) {
        res.render('admin_home');
    });

http.createServer(app).listen(app.get('port'), function () {
    console.log('Express server listening on port ' + app.get('port'));
});