const {google}=require('googleapis');
const MESSAGE_SCOPE="https://www.googleapis.com/auth/firebase.messaging";
const SCOPES=[MESSAGE_SCOPE];
// const http=require('http');

const express=require('express');
const app=express();
const bodyParser=require('body-parser');
const router=express.Router();

const request=require('request');
const port=4000;
app.use(bodyParser.urlencoded({extended:true}));
app.use(bodyParser.json());
app.use('/api',router);
app.listen(port,function(){
    console.log("Server is listening the port :"+port);
});
router.post("/send",(req,res)=>{

    getAccessToken().then(function(access_token){
        const tripFee=req.body.tripFee;
        const fromLocation=req.body.fromLocation;
        const toLocation=req.body.toLocation;
        const token=req.body.token;
        const tripId=req.body.tripId;

        request.post({
            headers:{
                Authorization:'Bearer '+access_token
            },
            url:"https://fcm.googleapis.com/v1/projects/maps-2019-4fb8e/messages:send",
            body:JSON.stringify(
                {
                    "message":{
                        "token":token,
                        "data":{
                          "tripFee":tripFee,
                          "fromLocation":fromLocation,
                          "toLocation":toLocation,
                          "tripId":tripId
                        }
                      }
                }
            )
    
        },function(error,response,body){
            res.end(body);
            console.log(body);
        });
    });

    
    // res.json({
    //     message:"hello keshar"
    // });
});


function getAccessToken(){
    return new Promise(function(resolve,reject){
        const key=require("./service-account.json");
        const jwtClient=new google.auth.JWT(
            key.client_email,
            null,
            key.private_key,
            SCOPES,
            null
        );
        jwtClient.authorize(function(error,token){
            if(error){
                reject(error);
                return;
            }
            resolve(token.access_token);
        });
    });
}




// const server=http.createServer(function(req,res){
//     getAccessToken().then(function(access_token){
//         res.end(access_token);
//     });
// });
// server.listen(3000,function(){
//     console.log("server started.")
// });