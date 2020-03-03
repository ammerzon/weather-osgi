jQuery(() => {
    const BASE_URL = "http://localhost:8181/weather/";
    const TEMPERATURE_URL = BASE_URL + "temperature";
    const RAINFALL_URL = BASE_URL + "rainfall";
    const CUMULATED_RAINFALL_URL = RAINFALL_URL + "?cumulated=true";
    const UPDATE_INTERVAL = 1000; // ms
    const MAX_POINTS = 200;

    const sensorTypes = {
        TEMPERATURE: 'temperature',
        RAINFALL: 'rainfall',
        CUMULATED_RAINFALL: 'cumulated-rainfall',
    };

    function getIdPrefix(sensorType) {
        switch (sensorType) {
            case sensorTypes.TEMPERATURE:
                return 'temperature';
            case sensorTypes.RAINFALL:
            case sensorTypes.CUMULATED_RAINFALL:
                return 'rainfall';
        }
    }

    function initTraces(sensorType) {
        let traces = [{name: 'current ' + sensorType, line: {shape: 'spline'}, x: [], y: []}];
        if (sensorType === sensorTypes.RAINFALL) {
            traces.push({name: 'current cumulated rainfall', line: {shape: 'spline'}, x: [], y: []})
        }

        Plotly.plot(getIdPrefix(sensorType) + 'Graph', traces);
    }

    function extend_traces(timeStamp, current, sensorType) {
        Plotly.extendTraces(getIdPrefix(sensorType) + 'Graph', {
            x: [[timeStamp]],
            y: [[current]],
        }, [sensorType !== sensorTypes.CUMULATED_RAINFALL ? 0 : 1], MAX_POINTS);
    }

    function resetTraces(sensorType) {
        Plotly.deleteTraces(getIdPrefix(sensorType) + 'Graph', [0]);
        initTraces(sensorType);
    }

    function handleError(statusCode, statusText, responseText, sensorType) {
        if (statusCode === 0) {
            $('#' + getIdPrefix(sensorType) + '-error-text').text(`Backend not reachable`);
        } else {
            $('#' + getIdPrefix(sensorType) + '-error-text').text(`${statusCode} (${statusText}): ${responseText}`);
        }

        $('#' + getIdPrefix(sensorType) + '-error-div').show();
        $('#' + getIdPrefix(sensorType) + 'Graph').hide();
        resetTraces(sensorType);
    }

    function createFetch(sensorType) {
        let url = '';
        switch (sensorType) {
            case sensorTypes.RAINFALL:
                url = RAINFALL_URL;
                break;
            case sensorTypes.TEMPERATURE:
                url = TEMPERATURE_URL;
                break;
            case sensorTypes.CUMULATED_RAINFALL:
                url = CUMULATED_RAINFALL_URL;
                break;
        }

        return fetch(url)
            .then((response) => {
                if (!response.ok) {
                    throw response;
                }
                return response.json();
            })
            .then((measurement) => {
                if (sensorType !== sensorTypes.CUMULATED_RAINFALL) {
                    $('#' + sensorType + '-error-div').hide();
                    $('#' + sensorType + 'Graph').show();
                }
                extend_traces(measurement.timeStamp, measurement.value, sensorType);
            }).catch(function (response) {
                response.text().then(responseMessage => {
                    handleError(response.status, response.statusText, responseMessage, sensorType);
                })
            });
    }

    async function fetchData() {
        const temperature = createFetch(sensorTypes.TEMPERATURE);
        const rainfall = createFetch(sensorTypes.RAINFALL);
        const cumulatedRainfall = createFetch(sensorTypes.CUMULATED_RAINFALL);

        await Promise.all([temperature, rainfall, cumulatedRainfall]);
        if (++count !== 200) {
            setTimeout(fetchData, UPDATE_INTERVAL);
        }
    }

    initTraces(sensorTypes.TEMPERATURE);
    initTraces(sensorTypes.RAINFALL);
    let count = 0;
    setTimeout(fetchData, UPDATE_INTERVAL);
});
