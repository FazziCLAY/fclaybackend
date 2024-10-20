package com.fazziclay.fclaybackend.person.status;

import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Statistic {
    private final List<PlaybackDto> stats = new ArrayList<>();

    public void appendStat(PlaybackDto status) {
        stats.add(status);
        if (stats.size() > 30) {
            stats.remove(0);
        }
    }

    // todo: so bad, need rewrite but no enough time for it
    public JsonObject asJsonObject() {
        var js = new JsonObject();
        js.addProperty("type", "line");

        var options = new JsonObject();
        var legend = new JsonObject();
        legend.addProperty("display", false);
        options.add("legend", legend);

        var data = new JsonObject();

        JsonArray labels = new JsonArray();
        for (PlaybackDto stat : stats) {
            labels.add(stat.getTitle());
        }
        data.add("labels", labels);

        var datasets = new JsonArray();

        JsonArray dataVolume = new JsonArray();
        JsonArray dataDuration = new JsonArray();

        for (PlaybackDto stat : stats) {
            dataVolume.add((int) (stat.getVolume() == null ? 50 : stat.getVolume() * 100));
            dataDuration.add((int) (stat.getDuration() == null ? 50 : stat.getDuration() / 1000));
        }

        var dataset1 = new JsonObject();
        dataset1.addProperty("fill", false);
        dataset1.addProperty("borderColor", "red");
        dataset1.add("data", dataVolume);
        datasets.add(dataset1);


        var dataset2 = new JsonObject();
        dataset2.addProperty("fill", false);
        dataset2.addProperty("borderColor", "green");
        dataset2.add("data", dataDuration);
        datasets.add(dataset2);

        data.add("datasets", datasets);


        js.add("options", options);
        js.add("data", data);

        return js;
    }
    //{
    //                type: "line",
    //                data: {
    //                    labels: xValues,
    //                    datasets: [{
    //                        data: [Math.random()*100,1140,1060,1060,1070,1110,1330,2210,7830,2478],
    //                        borderColor: "red",
    //                        fill: false
    //                    }, {
    //                        data: [1600,1700,1700,1900,2000,2700,4000,5000,6000,7000],
    //                        borderColor: "green",
    //                        fill: false
    //                    }, {
    //                        data: [300,700,2000,5000,6000,4000,2000,1000,200,100],
    //                        borderColor: "blue",
    //                        fill: false
    //                    }]
    //                },
    //                options: {
    //                    legend: {
    //                        display: false,
    //                        animation: {
    //                            duration: 0, // general animation time
    //                        },
    //                        hover: {
    //                            animationDuration: 0, // duration of animations when hovering an item
    //                        },
    //                        responsiveAnimationDuration: 0, // animation duration after a resi}
    //                    }
    //                }
    //            })
    //        }
}
