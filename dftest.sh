# Copyright 2019 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#!/bin/bash

# Keep track of PWD, before switching to script directory
# All commands are run relative to script directory
OLD_DIR=$PWD
CURRENT_DIR=$(dirname "$BASH_SOURCE")
cd $CURRENT_DIR

for sample_dir in $CURRENT_DIR/*/
do
    # Unzip zipped agent to ensure same as unzipped
    echo "Testing directory: $sample_dir"
    echo "Checking zipped and unzipped agents are identical..."
    unzip $sample_dir/dialogflow/agent.zip -d _test_unzipped > /dev/null 2>&1
    agent_diff=$(diff -r _test_unzipped $sample_dir/dialogflow/agent)
    if [[ $? != 0 ]]; then
        echo "Error: Make sure the zipped and unzipped agents are identical"
        rm -rf _test_unzipped
        exit 1
    fi
    echo "Success: Agents are identical"
    rm -rf _test_unzipped

    # Check whether strings match intents as intended
    # Currently does a very naive grep for string inclusion
    dialog=$sample_dir/dialogflow/test/dialog.csv
    all_intents_matched=true
    OLDIFS=$IFS
    IFS=,
    while read training_phrase intent
    do
        echo "Checking phrase $training_phrase matches $intent..."
        # Get matched filename as a string
        matched_path=$(grep $training_phrase $sample_dir/dialogflow/agent/intents/$intent.json)
        if [[ $? != 0 ]]; then
            echo "Error: Intent not matched for $training_phrase"
            all_intents_matched=false
        fi
    done < $dialog

    if [[ $all_intents_matched = true ]]; then
        echo "Success: dialog.csv matched all intents"
        echo
    else
        exit 1
    fi
    IFS=$OLDIFS

    cd $OLD_DIR
done
