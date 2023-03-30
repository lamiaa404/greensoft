import { createWebHistory, createRouter } from "vue-router";
import Home from "@/views/Home.vue";
import Analyze from "@/views/Analyze";
import Settings from "@/views/Settings.vue";
import Devices from "@/views/Devices";
import LastSequences from "@/views/LastSequences.vue";

const routes = [
    {
        path: "/",
        name: "Home",
        component: Home,
    },
    {
        path: "/analyze",
        name: "Analyze",
        component: Analyze,
    },
    {
        path: "/settings",
        name: "Settings",
        component: Settings,
    },
    {
        path: "/devices",
        name: "Devices",
        component: Devices,
    },
    {
        path: "/lastsequences",
        name: "LastSequences",
        component: LastSequences
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;