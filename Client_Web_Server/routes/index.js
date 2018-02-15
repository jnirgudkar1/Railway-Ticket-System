
/*
 * GET home page.
 */

exports.index = function(req, res){
  res.render('index', { title: 'Express' });
};


exports.session = function(req,res)
{
    console.log("!!!!");
    var username = req.query.username;
    var password = req.query.userID;

    console.log(req)
    console.log(username);
    console.log(password);

}

