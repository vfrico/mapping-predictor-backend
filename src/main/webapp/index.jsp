<!doctype html>
<html>

<script>
    function sendCreateTables() {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "webapi/installation/createtables", true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.send();
    }
    function sendAllCSV() {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "webapi/installation/addAllCSV", true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.send();
    }
</script>

<body>
    <h2>Incorrect mapping predictor for DBpedia</h2>
    <h3>Available resources</h3>
    <ul>
        <li>
            <a href="webapi/annotations">Annotation</a>
        </li>
    </ul>
<p>
    To start the server, please, execute those two API Calls:<br/>
    <a onclick="sendCreateTables()">Create tables</a><br/>
    <a onclick="sendAllCSV()">Add CSV</a>
</p>
</body>
</html>
