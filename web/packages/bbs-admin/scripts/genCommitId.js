import { exec } from 'child_process';
import { writeFile, readFile } from 'fs';
import { fileURLToPath } from 'node:url';
import { dirname, resolve } from 'node:path';

const filename = fileURLToPath(import.meta.url);
const dr = dirname(filename);
console.log(dr);

/**
 * 生成json文件
 */
function genJsonFile(json) {
  writeFile(resolve(dr, '../public/about.json'), json, 'utf8', (err) => {
    if (err) {
      console.error(err);
    } else {
      console.log('about.json 生成成功！');
    }
  });
}
/**
 * 获取commitId
 */
function getGitCommitId() {
  try {
    readFile(resolve(dr, '../package.json'), (err, data) => {
      console.log(data);
      if (err) {
        console.error(err);
      } else {
        const packageJson = JSON.parse(data);
        exec('git rev-parse HEAD', (error, stdout) => {
          const jsonData = {
            title: packageJson.name,
            version: packageJson.version,
            build: stdout.trim(),
          };
          genJsonFile(JSON.stringify(jsonData));
        });
      }
    });
  } catch (error) {
    console.log(error);
  }
}

getGitCommitId();
