import os

SBE_TOOL_PATH = "./sbe-all-1.35.6.jar"
TEMP_OUTPUT_DIR = "./temp_output_dir/"

CODECS_LOCATIONS = {
    "sbe_me_in": "./codecs/sbe_me_in.xml",
    "sbe_me_out": "./codecs/sbe_me_out.xml",
    "sbe_hub_inner": "./codecs/sbe_hub_inner.xml"
}

GEN_DATA = {
    "Java": {
        "sbe_me_in": ["../me/code/sbe", "../ingress_rest_server/code/sbe", "../ws_server/code/sbe"],
        "sbe_me_out": ["../me/code/sbe", "../egress_hub/code/cluster_archive_adapter/sbe", "../ws_server/code/sbe"],
        "sbe_hub_inner": ["../egress_hub/code/cluster_archive_adapter/sbe", "../egress_hub/code/me_egress_poller/sbe", "../egress_hub/code/hub_cluster_pusher/sbe"]
    },
    "Cpp": {
        "sbe_hub_inner": ["../egress_hub/code/cache_order_book_updater/sbe", "../egress_hub/code/cache_account_updater/sbe", "../egress_hub/code/db_trades_updater/sbe"],
        "sbe_me_out": ["../egress_hub/code/cache_order_book_updater/sbe", "../egress_hub/code/cache_account_updater/sbe", "../egress_hub/code/db_trades_updater/sbe"]
    }
}

def deleteDir(dir):
    command = "rm -rf " + dir
    os.system(command)

def copyDir(srcDir, destDir):
    makeDirCommand = "mkdir -p " + destDir
    os.system(makeDirCommand)
    
    command = "cp -R " + srcDir + "/* " + destDir
    os.system(command)

def runSbeGenerator(language, outputDir, codecsXml):
    global SBE_TOOL_PATH

    command = "java --add-opens java.base/jdk.internal.misc=ALL-UNNAMED "

    command += "-Dsbe.target.language=" + language + " "
    command += "-Dsbe.xinclude.aware=true" + " "
    command += "-Dsbe.output.dir=" + outputDir + " "
    command += "-jar " + SBE_TOOL_PATH + " "
    command += codecsXml

    print(command)
    os.system(command)
    
def cleanup():
    global GEN_DATA

    for language in GEN_DATA.keys():
        for codecsXml, copyDirs in GEN_DATA[language].items():
            tempOutputFileName = "sbe_generated_" + codecsXml + "_" + language
            tempOutputDir = TEMP_OUTPUT_DIR + tempOutputFileName

            deleteDir(tempOutputDir)

            for dir in copyDirs:
                deleteDir(dir)

def createTempFiles():
    global TEMP_OUTPUT_DIR
    global CODECS_LOCATIONS
    global GEN_DATA

    for language in GEN_DATA.keys():
        for codecsXml in GEN_DATA[language].keys():
            tempOutputFileName = "sbe_generated_" + codecsXml + "_" + language
            tempOutputDir = TEMP_OUTPUT_DIR + tempOutputFileName

            runSbeGenerator(language, tempOutputDir, CODECS_LOCATIONS[codecsXml])

def copySbe():
    global TEMP_OUTPUT_DIR
    global CODECS_LOCATIONS
    global GEN_DATA

    for language in GEN_DATA.keys():
        for codecsXml, copyDirs in GEN_DATA[language].items():
            tempOutputFileName = "sbe_generated_" + codecsXml + "_" + language
            tempOutputDir = TEMP_OUTPUT_DIR + tempOutputFileName

            for dir in copyDirs:
                copyDir(tempOutputDir, dir)

def main():
    cleanup()
    createTempFiles()
    copySbe()

if(__name__ == "__main__"):
    main()
