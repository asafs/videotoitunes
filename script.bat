@echo off
echo.CD=%CD%
echo %~dp0
cd C:\Users\Asaf\Documents\mp4converter
echo.CD=%CD%
"C:\Users\Asaf\Documents\mp4converter\Convert.py" "%~dp0"

pause