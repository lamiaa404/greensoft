import {Device} from "@/utils/Device";
import {Sequence} from "@/utils/Sequence";

// Generates an array of dummy devices and converts it to JSON. The size can be specified by the user via "amount".
// http://jsonviewer.stack.hu/
function generateDummyDeviceDataAsJSON(amount){
    let dummyData = [];

    generateDummyDeviceData(amount);

    return JSON.stringify(dummyData);
}

const names = [
    "Computer_",
    "Laptop_",
    "Raspberry Pi_",
    "Server_",
    "Office-PC_",
    "Smart Device_",
    "NAS_",
    "Router_",
    "Network Switch_"
]


// eslint-disable-next-line no-unused-vars
function generateDummyDeviceName(){
    return Math.floor(Math.random() * names.length);
}

// eslint-disable-next-line no-unused-vars
function generateDummyDeviceData(amount){
    let dummyData = [];
    const names = [
        "Computer_",
        "Laptop_",
        "Raspberry Pi_",
        "Server_",
        "Office-PC_",
        "Smart Device_",
        "NAS_",
        "Router_",
        "Network Switch_"
    ]

    for (let i = 0; i < amount; i++) {
        const tag = i % 2 === 0 ? "Computer" : "Server";
        const randomName = names[i % names.length];
        const newDevice = new Device(i, randomName + i, 100 + "W", "Uni Leipzig", 1, [tag]);
        dummyData.push(newDevice);
    }

    return dummyData;
}

function generateDummySequencesData(amount){
    let dummyData = [];
    const names = [
        "Computer_",
        "Laptop_",
        "Raspberry Pi_",
        "Server_",
        "Office-PC_",
        "Smart Device_",
        "NAS_",
        "Router_",
        "Network Switch_"
    ]

    for (let i = 0; i < amount; i++) {
        const randomName = names[i % names.length];
        const newSequence = new Sequence(i, "Sequenz_" + i, randomName + i, 5)
        dummyData.push(newSequence);
    }

    return dummyData;
}

// eslint-disable-next-line no-unused-vars
function generateDummyBarChartStatistics(start, end, amount){
    // Create Data objects from unix timestamps
    const startDate = new Date(start * 1000);
    const endDate = new Date(end * 1000);

    // Calculate the days in between these dates
    const diffInMs = endDate - startDate;
    const days = Math.floor(diffInMs / (1000 * 60 * 60 * 24));

    const data = [];

    let approximation = (x) => {
        return 0.5 + x - 5 * Math.pow(x,2) + 6 * Math.pow(x,3) - 1.8 * Math.pow(x,4);
    }

    const baselineConsumption = 12;

    for (let i = 0; i < amount; i++) {
        for (let j = 0; j < days; j++) {
           data.push({
               id: i,
               name: names[i % names.length] + i,
               consumption: approximation(j/days) * baselineConsumption * Math.max(Math.random(), 0.8),
               date: new Date(startDate.getTime() + (diffInMs/days * j))
           })
        }
    }

    //console.log(days);
    //console.log(data);

    return data;
}

export {generateDummyDeviceDataAsJSON, generateDummyDeviceData, generateDummySequencesData, generateDummyBarChartStatistics}