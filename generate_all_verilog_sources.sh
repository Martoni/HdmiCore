#!/usr/bin/bash

sbt "runMain hdmicore.Rgb2Tmds"
sbt "runMain hdmicore.TMDSEncoder"
sbt "runMain hdmicore.platforms.TangNano4k"
sbt "runMain hdmicore.platforms.TangNano9k"
sbt "runMain hdmicore.platforms.Ulx3s"
sbt "runMain hdmicore.video.HVSyncDriver"
