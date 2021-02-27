//Changes the label on the expression inputs to be correct to the selected format(sigma(Σ) for sum and pi(Π) for product)
function updateExpression(type, id){
    var label = "F=" + type + "(";
    document.getElementById(id).innerHTML = label;
}

//generates gray code corresponding to the given number
function generateGrayCode(num){
    return(num^(num>>>1));
}

//fills an array with gray codes in order
function generateCodes(max){
    var array = new Array(max);
    for(var i = 0; i < max; i++){
        array[i] = generateGrayCode(i);
    }

    return array;
}

//turn the input expressions into arrays of ints and strings being the binary representation of the ints
function getExpression(id){
    var expression = document.forms["kmapForm"][id].value;
    var expressionNums = expression.split(",");
    for(var i = 0; i < expressionNums.length; i++){
        expressionNums[i] = parseInt(expressionNums[i], 10);
    }
    return(expressionNums);
}

//turns a array of nums into an array of their binary representations as strings
function codesToString(codes){
    for(var i = 0; i < codes.length; i++){
        codes[i] = codes[i].toString(2);
    }
    return codes;
}

//finds the largest number from the two arrays
function largestNum(expression, dcs){
    var max = expression[expression.length-1];
    if(max < dcs[dcs.length-1]){
        max = dcs[dcs.length-1];
    }
    return max;
}

//finds the next power of 2 greater than the given number
function nextPower(num){
    var pow = 1;
    var count = 0;
    while(pow <= num){
        pow *= 2;
        count++;
    }
    return count;
}

function mapContent(num, e, dc){
    var type = document.getElementById("expressionLabel").innerHTML[2];
    var content = '1';
    var not = '0';
    if(type == 'Π'){
        content = '0';
        not = '1';
    }

    for(var i = 0; i < e.length; i++){
        if(e[i] == num){
            return content;
        }
    }
    for(var i = 0; i < dc.length; i++){
        if(dc[i] == num){
            return 'd';
        }
    }

    return not;
}

//prepend 0s onto string parts ensure that they are all the same length
function normalizeLengths(array){
    var length = array[array.length-1].length;
    for(var i = 0; i < array.length; i++){
        while(array[i].length < length){
            array[i] = "0" + array[i];
        }
    }
    return array;
}

//bubble sorts the expression
function sortExpression(expression){
    var flag = true;
    var dummy = expression[0];
    while(flag){
        flag = false;
        for(var i = 1; i < expression.length; i++){
            if(expression[i-1] > expression[i]){
                dummy = expression[i-1];
                expression[i-1] = expression[i];
                expression[i] = dummy;
                flag = true;
            }
        }
    }

    return expression;
}

//creates a 2d array.
function createArray(height, width){
    var x = new Array(height);

    for (var i = 0; i < x.length; i++) {
        x[i] = new Array(width);
    }
    return(x);
}

function generateMap(){
    var expression = sortExpression(getExpression("expression"));
    var dc = sortExpression(getExpression("dontCares"));

    var max = largestNum(expression, dc);

    console.log(max);

    max = nextPower(max);

    var yVar = max>>>1;
    var xVar = max-yVar;

    var xDim = Math.pow(2, xVar);
    var yDim = Math.pow(2, yVar);

    var codesX = generateCodes(xDim);
    var codesY = generateCodes(yDim);

    var xCodeString = normalizeLengths(codesToString(codesX));
    var yCodeString = normalizeLengths(codesToString(codesY));

    var map = createArray(yDim, xDim);
    var mapNum = createArray(yDim, xDim);

    var declaration = "";
    var declarationPiece = "";
    for(var i = 97; i <= 97 + yVar; i++){
        declarationPiece += String.fromCharCode(i);
    }
    declaration += "\\" + declarationPiece;
    declarationPiece = "";
    for(var i = 98 + yVar; i < 97 + yVar + xVar; i++){
        declarationPiece += String.fromCharCode(i);
    }
    declaration = declarationPiece + declaration;

    var output = "<tr><td>" + declaration + "</td>";
    for(var i = 0; i < xDim; i++){
        output += "<td>" + xCodeString[i] + "</td>";
    }
    for(var i = 0; i < yDim; i++){
        output += "<tr><td>" + yCodeString[i] + "</td>";
        for(var j = 0; j < xDim; j++){
            mapNum[i][j] = xCodeString[j] + yCodeString[i];
            map[i][j] = mapContent(parseInt(mapNum[i][j], 2), expression, dc);
            output += "<td>" + map[i][j] + "</td>";
        }
        output += "</tr>";
    }

    document.getElementById("map").innerHTML = output;
}