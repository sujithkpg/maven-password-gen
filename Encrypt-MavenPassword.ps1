# Prompt user for master password
$masterPassword = Read-Host "Enter the master password"

# Define Maven and Java paths
$mavenPath = "C:\Program Files\apache-maven-3.9.6"
$javaPath = "C:\Program Files\Amazon Corretto\jdk11.0.22_7"

# Create a temporary file to hold the password
$tempPasswordFile = [System.IO.Path]::GetTempFileName()
[System.IO.File]::WriteAllText($tempPasswordFile, $masterPassword)

# Prepare the Maven command
$mvnCommand = "`"$mavenPath\bin\mvn.cmd`" --encrypt-master-password -Dmaven.compiler.executable=`"$javaPath\bin\javac.exe`""

# Create command to be executed
$cmdArguments = "/c `"$mvnCommand`" < `"$tempPasswordFile`""

# Execute the Maven command using Start-Process
Start-Process -FilePath "cmd.exe" -ArgumentList $cmdArguments -NoNewWindow -Wait -RedirectStandardOutput "output.txt" -RedirectStandardError "error.txt"

# Read the Maven output from the output file
$output = Get-Content "output.txt" -Raw
$errorContent = Get-Content "error.txt" -Raw

# Clean up temporary files
Remove-Item $tempPasswordFile
Remove-Item "output.txt"
Remove-Item "error.txt"

# Display the encrypted password or error message
if ($errorContent) {
    Write-Error "Error occurred: $errorContent"
} else {
    Write-Output "Encrypted password: $output"
}
