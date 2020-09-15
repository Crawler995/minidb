import subprocess
import os

from config import minidb_launch_cmd, minidb_files


def __open_minidb_process():
    """
    启动minidb。
    """
    return subprocess.Popen(
        minidb_launch_cmd,
        shell=True,
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        encoding='utf-8'
    )

def __get_available_minidb_process_input(commands):
    """
    将commands数组转化为能直接给minidb进程的输入。
    """
    return '\n'.join(commands)

def __get_available_output_from_raw_output(raw_output):
    """
    将minidb进程的原始输出处理为可使用的输出。
    """
    ignored_outputs = [
        'db > ',
        'key > ',
        'value > '
    ]

    available_output = raw_output
    for ignored_output in ignored_outputs:
        available_output = available_output.replace(ignored_output, '')

    available_output = available_output.split('\n')[0:-1]

    return available_output

def __delete_existed_minidb_files():
    """
    删除config中定义的minidb_files。
    """
    for minidb_file in minidb_files:
        os.remove(minidb_file)

def run_commands_in_minidb(commands, delete_minidb_files_before_run=True):
    """
    传入类似 ['insert', '1', '11', 'get', '1']的命令数组commands， 
    输出相应的输出。

    @params delete_minidb_files_before_run: 如果为True，则运行之前先删除config中定义的minidb_files，可以避免污染环境。
    """
    if delete_minidb_files_before_run:
        __delete_existed_minidb_files()
    
    available_minidb_process_input = __get_available_minidb_process_input(commands)
    minidb_process = __open_minidb_process()

    raw_output = minidb_process.communicate(available_minidb_process_input)[0]
    available_output = __get_available_output_from_raw_output(raw_output)

    return available_output
