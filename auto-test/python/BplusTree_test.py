import unittest

from minidb_run import run_commands_in_minidb


class BplussTreeTest(unittest.TestCase):
    def test_insert_and_get_1(self):
        n = 100
        test_commands = []
        for i in range(1, n + 1):
            test_commands.append('insert')
            test_commands.append(str(i))
            test_commands.append(str(i) + str(i))
        for i in range(1, n + 1):
            test_commands.append('get')
            test_commands.append(str(i))

        expected_output = []
        for i in range(1, n + 1):
            expected_output.append('[%d%d]' % (i, i))

        real_output = run_commands_in_minidb(test_commands)
        
        self.assertListEqual(real_output, expected_output)

    def test_insert_and_get_2(self):
        n = 50
        test_commands = []
        for i in range(1, n + 1):
            test_commands.append('insert')
            test_commands.append(str(i))
            test_commands.append(str(i) + str(i))
            test_commands.append('insert')
            test_commands.append(str(i))
            test_commands.append(str(i) + str(i) + str(i))
        for i in range(1, n + 1):
            test_commands.append('get')
            test_commands.append(str(i))

        expected_output = []
        for i in range(1, n + 1):
            expected_output.append('[%d%d, %d%d%d]' % (i, i, i, i, i))

        real_output = run_commands_in_minidb(test_commands)
        
        self.assertListEqual(real_output, expected_output)

    def test_update_1(self):
        n = 50
        test_commands = []
        for i in range(1, n + 1):
            test_commands.append('insert')
            test_commands.append(str(i))
            test_commands.append(str(i) + str(i))
        for i in range(1, n + 1):
            test_commands.append('update')
            test_commands.append(str(i))
            test_commands.append(str(i) + str(i))
            test_commands.append(str(i) + str(i) + str(i))
        for i in range(1, n + 1):
            test_commands.append('get')
            test_commands.append(str(i))

        expected_output = []
        for i in range(1, n + 1):
            expected_output.append('[%d%d%d]' % (i, i, i))

        real_output = run_commands_in_minidb(test_commands)
        
        self.assertListEqual(real_output, expected_output)


if __name__ == "__main__":
    unittest.main()
