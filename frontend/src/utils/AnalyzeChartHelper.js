function getLabels (data){
    let labels = [];
    let dates = [];

    // eslint-disable-next-line no-unused-vars
    data.forEach((element, idx) => {
        // We just want to get the date and ignore time, so we later on have an array of dates like ["21.01", "22.01", ... ]
        const dateCandidate = new Date(element.date);
        dateCandidate.setHours(0,0,0,0);

        // Let's check if the date is already in our dates array
        if(!dates.includes(Math.floor(dateCandidate.getTime() / 1000))) {
            dates.push(Math.floor(dateCandidate.getTime() / 1000));
        }
    })

    // Sort dates
    dates.sort(function(a,b) {
        return a - b
    })


    // Generate label strings
    dates.forEach((element) => {
        labels.push(new Date(element * 1000).toLocaleDateString("de-DE"))
    })

    return labels
}

function getRandomColorSeeded(seed){
    const colorScheme = [
        "#25CCF7","#FD7272","#54a0ff","#00d2d3",
        "#1abc9c","#2ecc71","#3498db","#9b59b6","#34495e",
        "#16a085","#27ae60","#2980b9","#8e44ad","#2c3e50",
        "#ffcc20","#e67e22","#e74c3c","#ecf0f1","#95a5a6",
        "#f39c12","#d35400","#c0392b","#bdc3c7","#7f8c8d",
        "#55efc4","#81ecec","#74b9ff","#a29bfe","#dfe6e9",
        "#00b894","#00cec9","#0984e3","#6c5ce7","#ffeaa7",
        "#fab1a0","#ff7675","#fd79a8","#fdcb6e","#e17055",
        "#d63031","#feca57","#5f27cd","#54a0ff","#01a3a4"
    ]

    return colorScheme[seed % colorScheme.length]
}

function getLineChartDatasetsForSelectedIDs(data, selectedIDs){
    let dataArrays = []



    // Sort data by date in ascending order
    data = data.sort(({date:a}, {date:b}) => a-b);


    // We want to get every consumption of every device
    for (let i = 0; i < selectedIDs.length; i++) {

        let dataOfID = []

        for (let j = 0; j < data.length; j++) {


            if(data[j].id === selectedIDs[i]) {
                dataOfID.push(data[j].consumption);
            }
        }

        //const name = data.find(x => x.id === selectedIDs[i]).name

        // push object to dataset
        dataArrays.push({
            data: dataOfID,
            label: "ID:" + selectedIDs[i].toString() + " ",
            backgroundColor: getRandomColorSeeded(selectedIDs[i]),
            borderColor: getRandomColorSeeded(selectedIDs[i]),
            tension: .15,
            pointHitRadius: 50,
            hoverRadius: 10,
        });

    }

    return dataArrays
}

// eslint-disable-next-line no-unused-vars
function getBarChartDatasets (data, selectedDevices, type="mean") {
    // eslint-disable-next-line no-unused-vars
    let datasets = [
        { data: Array,
            label: "Auswahl",
            backgroundColor: "#a6f0c8"
        },
        { data: Array,
            label: "Verbleibende Geräte",
            backgroundColor: "#5b826d"
        }
    ];


    let selectedData = [];
    let unselectedData = [];

    // get all labels (x-values) first
    let labels = getLabels(data);

    // init arrays
    for (let i = 0; i < labels.length; i++) {
        selectedData[i] = 0;
        unselectedData[i] = 0;
    }



    labels.forEach((label, index) => {

        let selectedCount = 0;
        let selectedValue = 0;

        let unselectedCount = 0;
        let unselectedValue = 0;

        data.forEach(dataElement => {
            const dateCandidate = new Date(dataElement.date);
            dateCandidate.setHours(0,0,0,0);

            const unix = Math.floor(dateCandidate.getTime() / 1000);
            const dataDate = (new Date(unix * 1000).toLocaleDateString("de-DE"));

            // This is probably super slow because of String comparison. Quick and dirty will do for now.
            if(label === dataDate) {

                switch (type) {
                    case "mean":
                    case "idle": // for now, we calculate idle the same as mean

                        // If we want to get the average of each date by selected or unselected device, we need to add all values to a variable first
                        // and (later) divide by the count variable
                        if(selectedDevices.includes(dataElement.id)) {
                            // We add something to the selected dataset
                            selectedCount += 1
                            selectedValue += dataElement.consumption
                        } else {
                            // otherwise we will add something to unselected dataset
                            unselectedCount += 1
                            unselectedValue += dataElement.consumption
                        }

                        break;
                    case "max":

                        if(selectedDevices.includes(dataElement.id)) {

                            if(selectedValue < dataElement.consumption) {
                                selectedValue = dataElement.consumption
                            }

                        } else {
                            if(unselectedValue < dataElement.consumption) {
                                unselectedValue = dataElement.consumption
                            }
                        }

                        break;
                        default:
                        console.log("ERROR cannot generate Bar Chart Data due to unknown type: " + type)


                }
            }
        })

        switch (type) {
            case "mean":
            case "idle":
                selectedData[index] = selectedValue / selectedCount
                unselectedData[index] = unselectedValue / unselectedCount
                break;
            case "max":
                selectedData[index] = selectedValue
                unselectedData[index] = unselectedValue
                break;
            default:
                selectedData[index] = 0
                unselectedData[index] = 0

        }





    })

    datasets[0].data = selectedData;
    datasets[1].data = unselectedData;

    return datasets;
}

export {getLabels, getLineChartDatasetsForSelectedIDs, getBarChartDatasets, getRandomColorSeeded}