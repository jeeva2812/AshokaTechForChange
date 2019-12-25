const express = require('express');
const app = express();
const bp = require('body-parser');
const mysql = require('mysql2');
const bcrypt = require('bcrypt');
const moment = require('moment');
const util = require('util');
const SqlString = require('sqlstring');
const { parse } = require('json2csv');
const fs = require('fs');
// const exec = util.promisify(require('child_process').exec);
let {PythonShell} = require('python-shell')


const rec = "9wGVLuBUK08zfnYEPMr2ua4+3OO8K4WPqkpKUB2s";
const acc_sid = "ACfd9cff7c5a16ab410d4cb1e0653ed0ea";
const auth_token = "69ec6a0fb70e7a4a99fdb9d404eb12bb";
const client = require('twilio')(acc_sid, auth_token);
// TODOadd SqlString.escape()

app.use(bp.json());

const SQL_CREDENTIALS = {
	host: "localhost",
	user: "nodeapp",
	password: "nodeapp123",
	database: "radwalaDB"
};

app.post('/new_scrap_collector', async (req, res)=>{
	var name = req.body.name;
	var email = req.body.email;
	var license_no = req.body.license_no;
	var desc = req.body.desc;
	var mobile = req.body.mobile;
	var vehicle_cap = req.body.vehicle_cap; vehicle_cap = vehicle_cap == null ? 0 : vehicle_cap;
	var loc_lat = req.body.loc_lat;
	var loc_long = req.body.loc_long;

	var salt = bcrypt.genSaltSync(10);
	var hash_pw = bcrypt.hashSync(req.body.password, salt);

	// Building query
	var insertion_str = `'${name}', '${mobile}', ${vehicle_cap}, ${loc_lat}, ${loc_long}, '${hash_pw}'`;
	insertion_str = insertion_str + (desc == null ? '' : `,'${desc}'`) + (email == null ? '' : `,'${email}'`) + (license_no == null ? '' : `,'${license_no}'`)
	var columns_str = 'name, mobile, vehicle_cap, loc_lat, loc_long, password';
	columns_str = columns_str + (desc == null ? '' : `,description`) + (email == null ? '' : `,email`) + (license_no == null ? '' : `,license_no`)

	var sql = `INSERT INTO ScrapCollector (${columns_str}) VALUES (${insertion_str});`;

	const con = mysql.createConnection(SQL_CREDENTIALS);
	let err = await con.connect()
	if (err) throw err;
	let [result, fields] = await con.promise().query(sql)
	res.json({msg: "Added new Scrap Collector", id: result.insertId})
	con.end();
});

app.post('/new_user', async (req, res)=>{
	var mobile = req.body.mobile;
	console.log('new_user');
	var salt = bcrypt.genSaltSync(10);
	var hash_pw = bcrypt.hashSync(req.body.password, salt);

	// Building query
	var insertion_str = `'${mobile}','${hash_pw}'`;

	var columns_str = 'mobile, password';

	var sql = `INSERT INTO WasteProducer (${columns_str}) VALUES (${insertion_str});`;
	console.log(sql);
	console.log('connecting')

	const con = mysql.createConnection(SQL_CREDENTIALS);
	let err = await con.connect()
	if (err) { console.log(err); throw err; res.json({msg: 'Error'}); }
	else{
		console.log('working till else')
		let [result, fields] = await con.promise().query(sql)
		console.log('new_user sql done');
		con.end();
		res.json({msg: "Added new WasteProducer", id: result.insertId})
	}
});

app.post('/new_waste_request', async (req, res)=>{
	console.log("yay");
	var mobile = req.headers.mobile;
	var password = req.headers.password;
	let user_query = await validateUser(mobile, password)
	console.log("s#it");
	if(user_query.code == 1){
		var amount = req.body.amount;
		var lat = req.body.lat;
		var long = req.body.long;
		var comments = SqlString.escape(req.body.comments);
		var waste_type = req.body.waste_type;

		function rounder(num){
			// 0.005 accuracy => distance accuracy of 78m
			return Math.round(num*2000)/2000
		}

		// Building query
		var insertion_str = `'${mobile}', ${amount}, ${rounder(lat)}, ${rounder(long)}, ${waste_type}`;
		insertion_str = insertion_str + (comments == null ? '' : `,${comments}`);
		var columns_str = 'mobile, amount, lat, \`long\`, waste_type';
		columns_str = columns_str + (comments == null ? '' : `,comments`)

		var sql = `INSERT INTO WasteProduceLog (${columns_str}) VALUES (${insertion_str});`;
		console.log(sql);
		const con = mysql.createConnection(SQL_CREDENTIALS);
		let err = await con.connect()
		if (err){
			console.log('Ok');
			throw err; 
		}
		else{
			console.log('kya');
			let [result, fields] = await con.promise().query(sql)
			console.log(result)
			res.json({msg: "Added Waste Request", id: result.insertId});
			con.end();
		}
	}
	else
		res.json({msg:"Error"});	
});

async function validateScrapCollector(mobile, password){
	// Building query
	var selection_str = 'mobile, password, id';
	var where_clause = `mobile = '${mobile}'`
	var sql = `SELECT ${selection_str} FROM ScrapCollector WHERE ${where_clause};`;

	var code = -1; 
	var id = -1;

	const con = mysql.createConnection(SQL_CREDENTIALS);
	let err = await con.connect();
	if(err){
		console.log(err);
		throw err;
	}
	else{
		const [results, fields] = await con.promise().query(sql);
		if(results.length === 0)
			code = 0
		else{
			let res = await bcrypt.compare(password, results[0].password)
			if(res){
				code = 1;
				id = results[0].id
			}
			else
				code = 2
		}
	}
	con.end();
	console.log('code: '+code)
	return {code: code, id: id};
}

async function validateUser(mobile, password){
	// Building query
	var selection_str = 'mobile, password, id';
	var where_clause = `mobile = '${mobile}'`
	var sql = `SELECT ${selection_str} FROM WasteProducer WHERE ${where_clause};`;
	console.log(sql);
	var code = -1; 
	var id = -1;

	const con = mysql.createConnection(SQL_CREDENTIALS);
	let err = await con.connect();
	if(err){
		console.log(err);
		throw err;
	}
	else{
		const [results, fields] = await con.promise().query(sql);
		console.log(JSON.stringify(results));
		if(results.length === 0)
			code = 0
		else{
			let res = await bcrypt.compare(password, results[0].password)
			if(res){
				code = 1;
				id = results[0].id
			}
			else
				code = 2
		}
	}
	con.end();
	console.log('code: '+code)
	return {code: code, id: id};
}

async function getScrapCollectorLatLong(mobile){
	var sql = `SELECT loc_lat, loc_long, vehicle_cap FROM ScrapCollector WHERE mobile='${mobile}';`
	var return_result = null;

	const con = mysql.createConnection(SQL_CREDENTIALS);
	let err = await con.connect()
	if (err) throw err;
	else{
		let [results, fields] = await con.promise().query(sql)
		if(err) console.log(err)
		else return_result = {lat: results[0].loc_lat, long: results[0].loc_long, vehicle_cap: results[0].vehicle_cap}
		con.end();
	}
	return return_result
}

app.post('/login_scrap_collector', async (req, res)=>{
	var mobile = req.body.mobile;
	var password = req.body.password;
	let obj = await validateScrapCollector(mobile, password)
	switch(obj.code){
		case -1: res.json({msg: "Internal error", code: 500}); break;
		case 0: res.json({msg: "mobile not registered", code: 400}); break;
		case 1: res.json({code: 200}); break;
		case 2: res.json({msg: "Incorrect password", code: 400}); break;
	}
});

app.post('/login_user', async (req, res)=>{
	var mobile = req.body.mobile;
	var password = req.body.password;
	let obj = await validateUser(mobile, password)
	console.log(obj);
	switch(obj.code){
		case -1: res.json({msg: "Internal error", code: 500}); break;
		case 0: res.json({msg: "mobile not registered", code: 400}); break;
		case 1: res.json({code: 200}); break;
		case 2: res.json({msg: "Incorrect password", code: 400}); break;
	}
});

app.post('/waste_collected', async (req, res)=>{
	let mobile = req.headers.mobile;
	let password = req.headers.password;
	let user_query = await validateScrapCollector(mobile, password)
	if(user_query.code == 1){
		let log_id = req.body.log_id;

		// Building query
		var where_clause = `id = ${log_id} AND collected_by=${user_query.id}`
		var sql = `UPDATE WasteProduceLog
				   SET is_collected=1, collected_at='${moment().format('YYYY-MM-DD HH:mm:ss')}'
				   WHERE ${where_clause};`;

		const con = mysql.createConnection(SQL_CREDENTIALS);
		let err = await con.connect()
		if (err){
			throw err;
		}
		else{
			let [results, fields] = await con.promise().query(sql)
			res.json({msg: "Collected waste"})
			con.end();
		}
	}
	else
		res.json({msg: "Error"})
});

app.post('/get_uncollected_waste', async (req, res)=>{
	let mobile = req.headers.mobile;
	let password = req.headers.password;
	let user_query = await validateScrapCollector(mobile, password);

	if(user_query.code==1){
		let scrap_coll = await getScrapCollectorLatLong(mobile);
		let qd = req.body.qd; // in meters

		// convert distance to lat long difference
		let diff = qd/111045 // 1 degree = 111045 m
		min_lat = scrap_coll.lat - diff; max_lat = scrap_coll.lat + diff;
		min_long = scrap_coll.long - diff; max_long = scrap_coll.long + diff;

		let where_clause = `lat<=${max_lat} AND lat>=${min_lat} AND \`long\`<=${max_long} AND \`long\`>=${min_long} AND is_collected=0 AND collected_by is null`;
		var sql = `SELECT amount, lat, \`long\`, waste_type, mobile FROM WasteProduceLog WHERE ${where_clause};`
		// console.log(sql);
		const con = mysql.createConnection(SQL_CREDENTIALS);
		let err = await con.connect()
		if (err) throw err;
		else{
			let [results, fields] = await con.promise().query(sql);
			con.end();

			var wt_lat_long_mobile_phones = {};
			var wt_lat_long_amount = {};

			// console.log("results ")
			// console.log(results)

			results.forEach(result=>{
				if(wt_lat_long_amount[result.lat+'_'+result.long] == undefined){
					wt_lat_long_amount[result.lat+'_'+result.long] = new Array(4).fill(0);
					wt_lat_long_amount[result.lat+'_'+result.long][result.waste_type-1] = result.amount //-1 because id starts from 1
					
				}
				else{
					wt_lat_long_amount[result.lat+'_'+result.long][result.waste_type-1] += result.amount; //-1 because id starts from 1
					
				}

				if (wt_lat_long_mobile_phones[result.waste_type+'_'+result.lat+'_'+result.long] == undefined){
					wt_lat_long_mobile_phones[result.waste_type+'_'+result.lat+'_'+result.long] = [result.mobile];
				}
				else{
					wt_lat_long_mobile_phones[result.waste_type+'_'+result.lat+'_'+result.long].push(result.mobile);
				}
			});

			var optimization_array = [{'0':0, '1':0, '2':0,'3':0,lat: scrap_coll.lat, long: scrap_coll.long}]

			// Need to send wt_lat_long_amount for processing
			Object.keys(wt_lat_long_amount).forEach(lat_lng=>{
				let temp = lat_lng.split('_')
				let lat = parseFloat(temp[0]); let long = parseFloat(temp[1]);
				let amount = wt_lat_long_amount[lat_lng];
				//Need to send //TODODODOD
				optimization_array.push({
					'0': amount[0],
					'1': amount[1],
					'2': amount[2],
					'3': amount[3],
					lat: lat,
					long: long
				});
			});
			console.log(optimization_array)
			const csv = parse(optimization_array);
			// console.log(csv);
			fs.writeFileSync('temp.csv', csv);

			let options = {
			  mode: 'text',
			  pythonPath: './osmnx/bin/python3',
			  pythonOptions: [], // get print results in real-time
			  scriptPath: './',
			  args: [`${scrap_coll.vehicle_cap}`, `${qd}`]
			};

			PythonShell.run('final.py', options, function (err, results) {
			  if (err) throw err;
			  // results is an array consisting of messages collected during execution
			  else{
			  	console.log(JSON.parse(results[0]));
			  	res.json(JSON.parse(results[0]));
			  	sendSMS(JSON.parse(results[0]));

			  	var mobiles = {};
			  	Object.keys(wt_lat_long_mobile_phones).forEach((key)=>{
			  		wt_lat_long_mobile_phones[key].forEach((mob)=>{
			  			mobiles[mob] = 1
			  		})
			  	})
			  	var mobstr = JSON.stringify(Object.keys(mobiles))

			  	var sql = `UPDATE WasteProduceLog SET collected_by=${user_query.id} WHERE mobile in (${mobstr.substring(1,mobstr.length - 1)});`
			  	const con1 = mysql.createConnection(SQL_CREDENTIALS);
				con1.promise().query(sql).then(()=>{
					con1.end();
				})
			  }
			});

			// exec('source ./osmnx/bin/activate').then(()=>{
			// 	exec(`python final.py  `).then((out)=>{
			// 		res.json(out);
			// 		exec(`deactivate`);
			// 	})
			// })
		}	
	}
	else
		res.json({msg: "Error", code: 400})
});

app.post('/get_scrap_collectors', async (req, res)=>{
	let lat = req.body.lat;
	let lng = req.body.long;

	var where_clause = `sqrt(pow(${lat}-loc_lat,2) + pow(${lng}-loc_long,2))/111045 <= 20000`;
	var order_clause = `sqrt(pow(${lat}-loc_lat,2) + pow(${lng}-loc_long,2))/111045`;
	var sql = `SELECT name, mobile, email, description, loc_lat, loc_long FROM ScrapCollector 
			   WHERE ${where_clause}
			   ORDER BY ${order_clause}
			   LIMIT 20;`;

	const con = mysql.createConnection(SQL_CREDENTIALS);
	let err = await con.connect()
	if (err) throw err;
	let [results, fields] = await con.promise().query(sql);
	console.log(results);
	res.json(results);
	con.end();
});

app.get('/get_waste_logs', async (req, res)=>{
	var mobile = req.headers.mobile;
	var password = req.headers.password;
	let user_query = await validateUser(mobile, password);

	if(user_query.code == 1){
		var sql = `SELECT WasteProduceLog.id, WasteProduceLog.created_at, WasteProduceLog.collected_at, 
				   WasteProduceLog.waste_type, WasteProduceLog.comments, ScrapCollector.name as collected_by 
				   FROM WasteProduceLog
				   LEFT JOIN ScrapCollector ON WasteProduceLog.collected_by=ScrapCollector.id
				   WHERE WasteProduceLog.mobile='${mobile}'
				   ORDER BY WasteProduceLog.created_at DESC`;

		const con = mysql.createConnection(SQL_CREDENTIALS);
		let err = await con.connect();
		if (err) throw err;
		else{
			let [results, fields] = await con.promise().query(sql);
			results.forEach(result=>{
				result.created_at = moment(result.created_at).format("DD/MM/YYYY")
				if(result.collected_at!=null)
					result.collected_at = moment(result.collected_at).format("DD/MM/YYYY")
			})
			res.json(results)
			con.end();
		}
	}
	else res.json({msg: "error"})
});

app.get('/get_inventory', async (req, res)=>{
	var mobile = req.headers.mobile;
	var password = req.headers.password;
	let user_query = await validateScrapCollector(mobile, password);
	if(user_query.code == 1){
		var sql = `SELECT SUM(a.amount) as amount,a.waste_type as waste_type
				FROM
				  (
				    SELECT
				      amount,
				      waste_type
				    FROM
				      WasteProduceLog
				    WHERE
				      is_collected = 1
				      AND collected_by = ${user_query.id}
				  ) a
				GROUP BY
				  a.waste_type;`;
		console.log(sql)
		const con = mysql.createConnection(SQL_CREDENTIALS);
		let err = await con.connect();
		if (err) throw err;
		else{
			let [results, fields] = await con.promise().query(sql);
			console.log(results)
			res.json(results);
			con.end();
		}
	}
	else
		res.json({code: 400})
});

app.get('/', async (req, res)=>{
	console.log("YAAA");
})

async function sendSMS(obj){

}



app.listen(8000, () => console.log(`Example app listening on port 8000!`))