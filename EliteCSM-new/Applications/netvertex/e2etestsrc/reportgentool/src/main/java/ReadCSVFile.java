import com.sterlite.pcc.Logger;
import diameter.DiameterResultParser;
import rest.RestResultParser;
import util.FileUtil;

import java.io.File;

/**
 * Created by aditya on 16-09-17.
 */
public class ReadCSVFile {
    private static Logger log = new Logger();
    public static void main(String[] args) {
        log.info("=========================================================================================");
        log.info("HTML Report generation started");
        if (args.length < 3) {
            log.info("Please Execute command :");
            log.info("java -jar -diameter <INPUT FILE DIRECTORY> <OUTPUT FILE DIRECTORY> <TYPE> <RESULT FILE NAME> <REPORT FILE NAME> <MODULE NAME> [MAPPING FILE NAME]");
            log.info("OR");
            log.info("java -jar -rest <INPUT FILE DIRECTORY> <OUTPUT FILE DIRECTORY>");
            return;
        }

        if("-diameter".equals(args[0])== false && "-rest".equals(args[0])==false){
            log.info("invalid first argument");
            log.info("Options: -diameter, -rest");
            return;
        }

        if (FileUtil.checkIfDirectoryExists(args[1]) == false) {
            log.info(args[1] + " is not a directory");
            return;
        }

        if (FileUtil.checkIfDirectoryExists(args[2]) == false) {
            File tmpDir = new File(args[2]);
            tmpDir.mkdir();
        }

        if ("-diameter".equals(args[0])) {
            if (args.length < 7) {
                log.info("Please Execute command : " +
                        "java -jar -diameter <INPUT FILE DIRECTORY> <OUTPUT FILE DIRECTORY> <TYPE> <RESULT FILE NAME> <REPORT FILE NAME> <MODULE NAME> [MAPPING FILE NAME]");
                return;
            }

            String resultFile;
            if (FileUtil.checkIfFileExists(args[1]+File.separator+args[4]) == false) {
                log.info(args[4] + " does not exist at path " + args[1]);

                if(FileUtil.checkIfFileExists(args[4]) == false){
                    log.info(args[4] + " does not exist");
                    return;
                }else{
                    log.info(args[4] + " exists");
                    resultFile = args[4];
                }
            }else {
                resultFile = args[1]+File.separator+args[4];
            }

            FileUtil.copyMappingAndResultFile(resultFile, args[2]);
            FileUtil.copyNetverexData(args[1],args[2]);
            if (args.length == 7) {
                new DiameterResultParser().parse(args[2], resultFile, args[3], args[5], args[6]);
            } else {
                String mappingFile;
                if (FileUtil.checkIfFileExists(args[1] + File.separator + args[7]) == false) {
                    log.info(args[7] + " does not exist at path " + args[1]);

                    if (FileUtil.checkIfFileExists(args[7]) == false) {
                        log.info(args[7] + " does not exist");
                        return;
                    } else {
                        log.info(args[7] + " exists");
                        mappingFile = args[7];
                    }
                } else {
                    mappingFile = args[1] + File.separator + args[7];
                }
                FileUtil.copyMappingAndResultFile(mappingFile, args[2]);
                new DiameterResultParser().parse(args[2], resultFile, args[3], mappingFile, args[5], args[6]);
            }
            log.info("HTML Report generation completed");
            log.info("=========================================================================================");
            return;
        } else  if("-rest".equals(args[0])){
            String inputFolderPath = args[1]+File.separator;
            String ouputputFolderPath = args[2]+File.separator;

            FileUtil.copySMData(args[1],args[2]);
            RestResultParser.parse(inputFolderPath,ouputputFolderPath);
            log.info("HTML Report generation completed");
            log.info("=========================================================================================");
            return;
        }
        return;
    }

}
